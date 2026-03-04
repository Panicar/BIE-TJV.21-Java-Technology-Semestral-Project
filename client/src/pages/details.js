import API from '../api.js';

export async function renderDetailsPage(itemId) {
    const container = document.getElementById('app-content');
    container.innerHTML = `<div class="text-center mt-5"><div class="spinner-border text-primary"></div></div>`;

    try {
        const [item, reviews, avgData, countData, incoming, outgoing] = await Promise.all([
            API.items.getById(itemId),
            API.reviews.getByItem(itemId),
            API.reviews.getAverageRating(itemId),
            API.reviews.countForItem(itemId),
            API.connections.getToItem(itemId), 
            API.connections.getFromItem(itemId) 
        ]);

        const user = JSON.parse(localStorage.getItem('user'));
        const isAdmin = user && (user.role === 'ADMIN' || user.role === 'MODERATOR');
        const avgRating = avgData.averageRating || 0;
        const reviewCount = countData.count || 0;

        container.innerHTML = `
            <div class="row">
                <div class="col-lg-8">
                    <div class="card p-4 mb-4 border-0 shadow-sm">
                        <div class="d-flex justify-content-between align-items-start">
                            <div>
                                <span class="badge bg-dark text-primary mb-2">${item.itemType}</span>
                                <h1 class="auth-header mt-1">${item.name}</h1>
                                <p class="text-muted mb-4">${item.category}</p>
                            </div>
                            ${isAdmin ? `
                            <div class="d-flex gap-2">
                                <button class="btn btn-outline-warning btn-sm" id="edit-item-btn">Edit</button>
                                <button class="btn btn-outline-danger btn-sm" id="delete-item-btn">Delete</button>
                            </div>` : ''}
                        </div>
                            <div class="p-3 bg-dark rounded border border-secondary text-medium quill-content">
                                ${item.content}
                            </div>

                            <div class="mt-4">
                                <h5 class="text-muted small fw-bold mb-3">CONNECTIONS</h5>
                                <div class="d-flex flex-wrap gap-2">
                                    ${outgoing.map(c => `
                                        <div class="badge bg-dark border border-primary p-2">
                                            <span class="text-primary">${c.connectionType}</span> → ${c.toItemName}
                                        </div>
                                    `).join('')}
                                    ${incoming.map(c => `
                                        <div class="badge bg-dark border border-info p-2">
                                            ${c.fromItemName} → <span class="text-info">${c.connectionType}</span> THIS
                                        </div>
                                    `).join('')}
                                </div>
                            </div>
                    </div>

                    ${user ? `
                    <div class="card p-4 mb-4 bg-dark border-secondary shadow-sm" id="review-form-container">
                        <h5 class="auth-header mb-3" id="form-title">Write a Review</h5>
                        <form id="review-form">
                            <input type="hidden" id="edit-review-id" value="">
                            <div id="review-error" class="alert alert-danger small mb-3" style="display:none"></div>
                            <div class="mb-3">
                                <label class="form-label small">Rating</label>
                                <select class="form-control" id="review-rating" required>
                                    <option value="5">5 Stars</option>
                                    <option value="4">4 Stars</option>
                                    <option value="3" selected>3 Stars</option>
                                    <option value="2">2 Stars</option>
                                    <option value="1">1 Star</option>
                                </select>
                            </div>
                            <div class="mb-3">
                                <label class="form-label small">Comment</label>
                                <textarea class="form-control" id="review-comment" rows="3"></textarea>
                            </div>
                            <button type="submit" class="btn btn-primary btn-sm px-4 fw-bold" id="submit-review-btn">Submit Review</button>
                            <button type="button" class="btn btn-link btn-sm text-muted" id="cancel-edit-btn" style="display:none">Cancel Edit</button>
                        </form>
                    </div>` : ''}

                    <h4 class="auth-header mb-3">Community Reviews (${reviewCount})</h4>
                    <div id="reviews-list">
                        ${reviews.map(r => {
                            const isAuthor = user && r.userId === user.id;
                            const displayDate = r.reviewDate 
                                ? new Date(r.reviewDate).toLocaleDateString(undefined, { 
                                    year: 'numeric', 
                                    month: 'short', 
                                    day: 'numeric',
                                    hour: '2-digit',
                                    minute: '2-digit'
                                }) 
                                : 'Just now';
                            return `
                                <div class="card mb-3 p-3 bg-dark border-0 shadow-sm border-start border-primary border-3">
                                    <div class="d-flex justify-content-between align-items-center">
                                        <div class="d-flex align-items-center">
                                            <div class="bg-secondary rounded-circle d-flex align-items-center justify-content-center me-2" style="width: 32px; height: 32px;">
                                                <i class="fas fa-user text-white-50 small"></i>
                                            </div>
                                            <span class="text-primary fw-bold">${r.username}</span>
                                        </div>
                                        <div class="d-flex align-items-center gap-3">
                                            <span class="text-warning small">${'★'.repeat(r.rating)}${'☆'.repeat(5-r.rating)}</span>
                                            <div class="d-flex gap-2">
                                                ${isAuthor ? `
                                                    <button class="btn btn-sm btn-outline-info border-0 p-1 edit-rev" 
                                                            title="Edit Review"
                                                            data-id="${r.id}" 
                                                            data-rating="${r.rating}" 
                                                            data-comment="${r.comment}">
                                                        <i class="fas fa-edit"></i>
                                                    </button>` : ''}
                                                ${(isAuthor || isAdmin) ? `
                                                    <button class="btn btn-sm btn-outline-danger border-0 p-1 delete-rev" 
                                                            title="Delete Review"
                                                            data-id="${r.id}">
                                                        <i class="fas fa-trash-alt"></i>
                                                    </button>` : ''}
                                            </div>
                                        </div>
                                    </div>
                                    <div class="ms-4 ps-2 border-start border-secondary mt-2">
                                        <p class="small text-medium mb-1">${r.comment || '<i class="text-muted">No comment provided.</i>'}</p>
                                        <small class="text-muted d-block mt-1" style="font-size: 0.7rem;">
                                            <i class="far fa-clock me-1"></i>${displayDate}
                                        </small>
                                    </div>
                                </div>`;
                        }).join('')}
                    </div>
                </div>
                <div class="col-lg-4">
                    <div class="card p-4 text-center sticky-top border-0 shadow-sm" style="top: 100px;">
                        <h5 class="text-muted small">AVERAGE RATING</h5>
                        <h1 class="display-3 fw-bold text-warning">${avgRating.toFixed(1)}</h1>
                        <p class="text-muted mb-4">based on ${reviewCount} reviews</p>
                        <button class="btn btn-outline-secondary w-100" onclick="window.navigateTo('/')">← Back</button>
                    </div>
                </div>
            </div>
        `;

        setupReviewLogic(itemId, user);
        if (isAdmin) setupAdminLogic(item.id);

    } catch (err) {
        container.innerHTML = `<div class="alert alert-danger mt-5">Error: ${err.message}</div>`;
    }
}


function setupReviewLogic(itemId, user) {
    if (!user) return;

    const form = document.getElementById('review-form');
    const cancelBtn = document.getElementById('cancel-edit-btn');
    const title = document.getElementById('form-title');
    const submitBtn = document.getElementById('submit-review-btn');
    const editIdInput = document.getElementById('edit-review-id');

    // 1. Handle Delete Review
    document.querySelectorAll('.delete-rev').forEach(btn => {
        btn.onclick = async () => {
            if (confirm("Permanently delete this review?")) {
                try {
                    await API.reviews.delete(btn.dataset.id);
                    renderDetailsPage(itemId); // Refresh view
                } catch (err) {
                    alert("Delete failed: " + err.message);
                }
            }
        };
    });

    // 2. Handle Edit Trigger
    document.querySelectorAll('.edit-rev').forEach(btn => {
        btn.onclick = () => {
            // Switch UI to Edit Mode
            title.innerText = "Editing Your Review";
            submitBtn.innerText = "Save Changes";
            cancelBtn.style.display = "inline-block";
            
            // Fill the form with existing data
            editIdInput.value = btn.dataset.id;
            document.getElementById('review-rating').value = btn.dataset.rating;
            document.getElementById('review-comment').value = btn.dataset.comment;
            
            // Smooth scroll to form
            form.scrollIntoView({ behavior: 'smooth', block: 'center' });
        };
    });

    // Reset form to "Create Mode"
    const resetForm = () => {
        form.reset();
        editIdInput.value = "";
        title.innerText = "Write a Review";
        submitBtn.innerText = "Submit Review";
        cancelBtn.style.display = "none";
    };

    cancelBtn.onclick = resetForm;

    // 3. Handle Submit (Fixed Update Logic)
    form.onsubmit = async (e) => {
        e.preventDefault();
        
        const reviewId = editIdInput.value;
        const submitSpinner = document.getElementById('review-spinner');
        
        // Construct DTO
        const payload = {
            userId: user.id,
            itemId: parseInt(itemId),
            rating: parseInt(document.getElementById('review-rating').value),
            comment: document.getElementById('review-comment').value.trim()
        };

        submitBtn.disabled = true;
        if (submitSpinner) submitSpinner.style.display = 'inline-block';

        try {
            if (reviewId) {
                // UPDATE MODE: PUT /api/reviews/{id}
                // Ensure your API.js update method accepts (id, data)
                await API.reviews.update(reviewId, payload);
            } else {
                // CREATE MODE: POST /api/reviews
                await API.reviews.create(payload);
            }
            
            resetForm();
            renderDetailsPage(itemId);
        } catch (err) {
            const errEl = document.getElementById('review-error');
            errEl.innerText = "Error: " + err.message;
            errEl.style.display = 'block';
            submitBtn.disabled = false;
            if (submitSpinner) submitSpinner.style.display = 'none';
        }
    };
}

function setupAdminLogic(itemId) {
    const deleteBtn = document.getElementById('delete-item-btn');
    const editBtn = document.getElementById('edit-item-btn');

    if (editBtn) {
        editBtn.onclick = () => window.navigateTo(`/editor/${itemId}`);
    }

    if (deleteBtn) {
        deleteBtn.onclick = async () => {
            const confirmDelete = confirm(
                "Warning: Deleting this item will also remove all associated reviews and connections. Proceed?"
            );

            if (confirmDelete) {
                try {
                    // Show loading state on button
                    deleteBtn.disabled = true;
                    deleteBtn.innerHTML = `<span class="spinner-border spinner-border-sm"></span> Removing...`;

                    // CALL: DELETE /api/epistemic-items/{id}
                    await API.items.delete(itemId);
                    
                    // Success: Redirect to home
                    window.navigateTo('/');
                } catch (err) {
                    deleteBtn.disabled = false;
                    deleteBtn.innerText = "Delete";
                    
                    // Specific error handling for constraints
                    if (err.message.includes("constraint")) {
                        alert("Delete Failed: This item is still linked to other theories. Please remove connections first.");
                    } else {
                        alert("System Error: " + err.message);
                    }
                }
            }
        };
    }
}