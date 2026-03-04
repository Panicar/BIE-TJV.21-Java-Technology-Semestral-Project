import API from '../api.js';

export async function renderConnectionsPage() {
    const container = document.getElementById('app-content');
    container.innerHTML = `<div class="text-center mt-5"><div class="spinner-border text-primary"></div></div>`;

    try {
        const [connections, items] = await Promise.all([
            API.connections.getAll(),
            API.items.getAll()
        ]);

        const user = JSON.parse(localStorage.getItem('user'));
        const canManage = user && (user.role === 'ADMIN' || user.role === 'MODERATOR');

        container.innerHTML = `
            <div class="container mt-4 pb-5">
                <h2 class="auth-header mb-4 text-info">
                    <i class="fas fa-project-diagram me-2"></i>Knowledge Graph
                </h2>
                
                ${canManage ? `
                <div class="card bg-dark border-info mb-5 p-4 shadow">
                    <h5 class="text-info mb-3">Create New Connection</h5>
                    <form id="connection-form" class="row g-3">
                        <div class="col-md-3">
                            <label class="form-label small text-muted">SOURCE ITEM</label>
                            <select class="form-select bg-black text-white border-secondary" id="from-item" required>
                                <option value="">Select...</option>
                                ${items.map(i => `<option value="${i.id}">${i.name}</option>`).join('')}
                            </select>
                        </div>
                        <div class="col-md-2">
                            <label class="form-label small text-muted">RELATION</label>
                            <select class="form-select bg-black text-white border-secondary" id="con-type">
                                <option value="SUPPORTS">SUPPORTS</option>
                                <option value="CONTRADICTS">CONTRADICTS</option>
                                <option value="RELATES">RELATES</option>
                                <option value="COMPLEMENTS">COMPLEMENTS</option>
                            </select>
                        </div>
                        <div class="col-md-2">
                            <label class="form-label small text-muted">STRENGTH (1-10)</label>
                            <input type="number" id="con-strength" class="form-control bg-black text-white border-secondary" min="1" max="10" value="5" required>
                        </div>
                        <div class="col-md-3">
                            <label class="form-label small text-muted">TARGET ITEM</label>
                            <select class="form-select bg-black text-white border-secondary" id="to-item" required>
                                <option value="">Select...</option>
                                ${items.map(i => `<option value="${i.id}">${i.name}</option>`).join('')}
                            </select>
                        </div>
                        <div class="col-md-2 d-flex align-items-end">
                            <button type="submit" class="btn btn-info w-100 fw-bold">Link Items</button>
                        </div>
                    </form>
                </div>` : ''}

                <div class="row row-cols-1 row-cols-md-2 g-4">
                    ${connections.length > 0 ? connections.map(c => `
                        <div class="col">
                            <div class="card bg-dark border-secondary h-100 p-3 shadow-sm border-opacity-25">
                                <div class="d-flex justify-content-between align-items-center mb-2">
                                    <span class="text-primary fw-bold small text-truncate" style="max-width: 40%;">${c.fromItemName || 'Item ' + c.fromItemId}</span>
                                    <div class="text-center px-2">
                                        <i class="fas fa-link text-info d-block small mb-1"></i>
                                        <span class="badge bg-outline-info border border-info text-info extra-small">
                                            ${c.connectionType} (S: ${c.strength})
                                        </span>
                                    </div>
                                    <span class="text-success fw-bold small text-truncate" style="max-width: 40%; text-align: right;">${c.toItemName || 'Item ' + c.toItemId}</span>
                                </div>
                                
                                ${canManage ? `
                                <div class="d-flex gap-2 mt-3 border-top border-secondary pt-2">
                                    <button class="btn btn-sm btn-outline-warning flex-grow-1" onclick="handleEditConnection(${c.id}, ${c.strength})">
                                        <i class="fas fa-edit me-1"></i>Edit Strength
                                    </button>
                                    <button class="btn btn-sm btn-outline-danger flex-grow-1" onclick="handleDeleteConnection(${c.id})">
                                        <i class="fas fa-trash me-1"></i>Remove
                                    </button>
                                </div>` : ''}
                            </div>
                        </div>
                    `).join('') : '<div class="col-12 text-center text-muted py-5">No connections found in the knowledge graph.</div>'}
                </div>
            </div>
        `;

        setupFormListener(canManage);
        setupGlobalHandlers();

    } catch (err) {
        container.innerHTML = `<div class="alert alert-danger mt-5">Error: ${err.message}</div>`;
    }
}

function setupFormListener(canManage) {
    if (!canManage) return;
    document.getElementById('connection-form').onsubmit = async (e) => {
        e.preventDefault();
        const fromId = document.getElementById('from-item').value;
        const toId = document.getElementById('to-item').value;
        const strength = document.getElementById('con-strength').value;

        if (fromId === toId) return alert("You cannot connect an item to itself.");

        const payload = {
            fromItemId: parseInt(fromId),
            toItemId: parseInt(toId),
            connectionType: document.getElementById('con-type').value,
            strength: parseInt(strength)
        };

        try {
            await API.connections.create(payload); // POST /api/connections
            renderConnectionsPage(); 
        } catch (err) {
            alert("Create failed: " + err.message);
        }
    };
}

function setupGlobalHandlers() {
    window.handleDeleteConnection = async (id) => {
        if (!confirm("Are you sure you want to remove this connection?")) return;
        try {
            await API.connections.delete(id); // DELETE /api/connections/{id}
            renderConnectionsPage();
        } catch (err) {
            alert("Delete failed: " + err.message);
        }
    };

    window.handleEditConnection = async (id, currentStrength) => {
        const newStrength = prompt("Update Connection Strength (1-10):", currentStrength);
        if (newStrength === null || newStrength === "") return;
        
        const strengthInt = parseInt(newStrength);
        if (isNaN(strengthInt) || strengthInt < 1 || strengthInt > 10) {
            return alert("Please enter a valid number between 1 and 10.");
        }

        try {
            // Partial update - usually requires getting the full object first or a PATCH endpoint
            const existing = (await API.connections.getAll()).find(c => c.id === id);
            const payload = { ...existing, strength: strengthInt };
            
            await API.connections.update(id, payload); // PUT /api/connections/{id}
            renderConnectionsPage();
        } catch (err) {
            alert("Update failed: " + err.message);
        }
    };
}