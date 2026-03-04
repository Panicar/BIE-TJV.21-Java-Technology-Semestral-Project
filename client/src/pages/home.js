import API from '../api.js';

/**
 * Renders the Home Page with a library of Epistemic Items.
 * Updated to use window.navigateTo for clean URL routing.
 */
export async function renderHomePage() {
    const container = document.getElementById('app-content');
    
    // Show loading state
    container.innerHTML = `
        <div class="text-center mt-5">
            <div class="spinner-border text-primary" role="status"></div>
            <p class="mt-2 text-muted">Loading Knowledge Library...</p>
        </div>`;

    try {
        // Fetch all items (Theories and Statements) from the API
        const items = await API.items.getAll(); 
        
        if (!items || items.length === 0) {
            container.innerHTML = `<h2 class="auth-header mb-4">Knowledge Library</h2><p class="text-muted">No items found.</p>`;
            return;
        }

        let html = `<h2 class="auth-header mb-4">Knowledge Library</h2><div class="row g-4">`;
        
        items.forEach(item => {
            const typeColor = item.itemType === 'THEORY' ? 'text-primary' : 'text-success'; 
            
            // Use a helper to strip HTML tags if you are using Quill/Rich Text
            const plainText = item.content.replace(/<[^>]*>?/gm, '');

            html += `
                <div class="col-md-6 col-lg-4">
                    <div class="card h-100 shadow-sm border-0">
                        <div class="card-body d-flex flex-column">
                            <span class="badge bg-dark ${typeColor} mb-2" style="width: fit-content;">
                                ${item.itemType}
                            </span>
                            <h5 class="card-title fw-bold">${item.name}</h5>
                            <h6 class="text-secondary small mb-3">${item.category}</h6>
                            
                            <p class="card-text text-muted small flex-grow-1" 
                            style="height: 4.5em; line-height: 1.5em; overflow: hidden; display: -webkit-box; -webkit-line-clamp: 3; -webkit-box-orient: vertical;">
                                ${plainText}
                            </p>

                            <button class="btn btn-outline-primary btn-sm w-100 mt-3 view-details-btn" 
                                    onclick="window.navigateTo('/details/${item.id}')">
                                View Details
                            </button>
                        </div>
                    </div>
                </div>`;
        });
        
        html += `</div>`;
        container.innerHTML = html;

    } catch (err) {
        container.innerHTML = `
            <div class="alert alert-danger shadow-sm">
                <p class="mb-0 fw-bold">Connection Error</p>
                <small>${err.message}</small>
            </div>`;
    }
}