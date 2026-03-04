import API from '../api.js';

/**
 * Renders the Interdisciplinary Analysis page.
 * This satisfies the "Complex Business Logic" requirement by coordinating 
 * multiple data operations to provide a single analytical insight.
 */
export async function renderAnalysisPage() {
    const container = document.getElementById('app-content');
    
    // STEP 1: User Input UI
    // We use the 'category' field from the epistemic_item table to filter data.
    container.innerHTML = `
        <div class="card bg-dark border-primary p-4 shadow-lg mb-4">
            <h2 class="text-primary mb-3">
                <i class="fas fa-brain me-2"></i>Interdisciplinary Analysis
            </h2>
            <p class="text-muted small">
                Compare categories to find hidden links between high-quality items 
                across different fields of study.
            </p>
            <div class="row g-3">
                <div class="col-md-5">
                    <label class="small text-muted fw-bold text-uppercase">Primary Category</label>
                    <input type="text" id="cat-1" class="form-control bg-black text-white border-secondary" 
                           placeholder="e.g., Biology / Medical Science">
                </div>
                <div class="col-md-5">
                    <label class="small text-muted fw-bold text-uppercase">Secondary Category</label>
                    <input type="text" id="cat-2" class="form-control bg-black text-white border-secondary" 
                           placeholder="e.g., Philosophy of Science">
                </div>
                <div class="col-md-2 d-flex align-items-end">
                    <button id="run-analysis" class="btn btn-primary w-100 fw-bold">
                        <i class="fas fa-microscope me-1"></i> Analyze
                    </button>
                </div>
            </div>
        </div>
        <div id="analysis-results"></div>
    `;

    document.getElementById('run-analysis').onclick = runComplexAnalysis;
}

/**
 * Orchestrates multiple API calls to perform complex business logic.
 */

async function runComplexAnalysis() {
    const resultsDiv = document.getElementById('analysis-results');
    const cat1 = document.getElementById('cat-1').value.trim();
    const cat2 = document.getElementById('cat-2').value.trim();

    resultsDiv.innerHTML = `<div class="text-center p-5"><div class="spinner-border text-primary"></div></div>`;

    try {
        // STEP 1: Fetch interdisciplinary connections (Complex JPQL Query)
        //
        const response = await fetch(`http://localhost:8081/api/connections/analysis?cat1=${encodeURIComponent(cat1)}&cat2=${encodeURIComponent(cat2)}`);
        
        const connections = await response.json();

        // Safety Check: If the backend returns an error object instead of an array
        if (!Array.isArray(connections)) {
            throw new Error(connections.message || "Invalid data format received from server");
        }

        const reportData = [];

        // STEP 2: Orchestrate multiple data operations (Fetching reviews for quality metrics)
        //
        for (const conn of connections) {
            // Fetch average ratings from the Review table
            const ratingA = await API.reviews.getAverageRating(conn.fromItemId).catch(() => ({ averageRating: 0 }));
            const ratingB = await API.reviews.getAverageRating(conn.toItemId).catch(() => ({ averageRating: 0 }));
            
            reportData.push({
                itemA: conn.fromItemName, // Mapping from ConnectionDTO
                itemB: conn.toItemName,   // Mapping from ConnectionDTO
                strength: conn.strength,
                avgRating: ((ratingA.averageRating || 0) + (ratingB.averageRating || 0)) / 2
            });
        }
        
        // Pass the guaranteed array to the display function
        displayAnalysisReport(reportData, cat1, cat2);
    } catch (err) {
        resultsDiv.innerHTML = `<div class="alert alert-danger">Analysis Failed: ${err.message}</div>`;
    }
}

function displayAnalysisReport(data, cat1, cat2) {
    const resultsDiv = document.getElementById('analysis-results');
    
    // Safety check inside display function to prevent .map() crash
    if (!Array.isArray(data)) {
        resultsDiv.innerHTML = `<div class="alert alert-warning">No data available for this category pair.</div>`;
        return;
    }

    resultsDiv.innerHTML = `
        <div class="card bg-dark border-secondary p-4 shadow">
            <h4 class="text-info border-bottom border-secondary pb-3 mb-4">
                ${cat1} <i class="fas fa-link mx-2 small"></i> ${cat2}
            </h4>
            ${data.length > 0 ? data.map(d => `
                <div class="card bg-black border-secondary mb-3 p-3">
                    <div class="row align-items-center">
                        <div class="col-md-5">
                            <span class="text-primary fw-bold d-block">${d.itemA}</span>
                            <span class="text-warning small">${'★'.repeat(Math.round(d.avgRating))}</span>
                        </div>
                        <div class="col-md-2 text-center text-muted">
                            <span class="badge border border-info text-info">${d.strength} Str</span>
                        </div>
                        <div class="col-md-5 text-end">
                            <span class="text-success fw-bold d-block">${d.itemB}</span>
                        </div>
                    </div>
                </div>
            `).join('') : '<p class="text-muted text-center py-4">No interdisciplinary connections found.</p>'}
        </div>`;
}