import API from '../api.js';

let quill; 

export async function renderEditorPage(itemId = null) {
    const container = document.getElementById('app-content');
    let itemData = { name: '', content: '', category: '', itemType: 'THEORY' };

    if (itemId) {
        container.innerHTML = `<div class="text-center mt-5"><div class="spinner-border text-primary"></div></div>`;
        try {
            itemData = await API.items.getById(itemId);
        } catch (err) {
            container.innerHTML = `<div class="alert alert-danger">Error: ${err.message}</div>`;
            return;
        }
    }

    container.innerHTML = `
        <div class="row justify-content-center">
            <div class="col-md-10">
                <div class="card p-4 shadow border-0 bg-dark text-light">
                    <h3 class="auth-header mb-4 text-primary">
                        <i class="fas fa-pen-nib me-2"></i>${itemId ? 'Edit' : 'Create'} Epistemic Item
                    </h3>
                    
                    <form id="editor-form">
                        <div class="row mb-4">
                            <div class="col-md-6">
                                <label class="form-label fw-bold text-muted small">TITLE</label>
                                <input type="text" class="form-control bg-black text-white border-secondary" id="item-name" value="${itemData.name}" required>
                            </div>
                            <div class="col-md-3">
                                <label class="form-label fw-bold text-muted small">CATEGORY</label>
                                <input type="text" class="form-control bg-black text-white border-secondary" id="item-category" value="${itemData.category}" placeholder="e.g. Biology" required>
                            </div>
                            <div class="col-md-3">
                                <label class="form-label fw-bold text-muted small">TYPE</label>
                                <select class="form-control bg-black text-white border-secondary" id="item-type">
                                    <option value="THEORY" ${itemData.itemType === 'THEORY' ? 'selected' : ''}>THEORY</option>
                                    <option value="STATEMENT" ${itemData.itemType === 'STATEMENT' ? 'selected' : ''}>STATEMENT</option>
                                </select>
                            </div>
                        </div>

                        <div class="mb-4">
                            <label class="form-label fw-bold text-muted small">CONTENT</label>
                            <div id="quill-editor" style="height: 300px; background: #000;"></div>
                        </div>

                        <div class="d-flex gap-3">
                            <button type="submit" class="btn btn-primary px-5 rounded-pill fw-bold">
                                <i class="fas fa-save me-2"></i>Save Item
                            </button>
                            <button type="button" class="btn btn-outline-secondary rounded-pill" onclick="navigateTo('/')">Cancel</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>`;

    quill = new Quill('#quill-editor', {
        theme: 'snow',
        modules: {
            toolbar: [
                [{ 'header': [1, 2, 3, false] }],
                ['bold', 'italic', 'underline'],
                [{ 'list': 'ordered'}, { 'list': 'bullet' }],
                ['blockquote', 'code-block'],
                ['clean'] 
            ]
        }
    });

    if (itemData.content) {
        quill.root.innerHTML = itemData.content;
    }

    document.getElementById('editor-form').onsubmit = async (e) => {
        e.preventDefault();
        
        const payload = {
            name: document.getElementById('item-name').value.trim(),
            itemType: document.getElementById('item-type').value,
            // FIXED: Capturing value from the new category input
            category: document.getElementById('item-category').value.trim(),
            content: quill.root.innerHTML 
        };

        try {
            if (itemId) {
                await API.items.update(itemId, payload);
            } else {
                await API.items.create(payload);
            }
            navigateTo('/');
        } catch (err) {
            alert("Failed to save: " + err.message);
        }
    };
}