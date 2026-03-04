import { renderHomePage } from './pages/home.js';
import { renderLoginPage, renderSignupPage } from './pages/auth.js';
import { renderProfilePage } from './pages/profile.js';
import { renderDetailsPage } from './pages/details.js';
import { renderEditorPage } from './pages/editor.js';
import { renderConnectionsPage } from './pages/connections.js';
import { renderAdminPage } from './pages/admin.js';
import { renderAnalysisPage } from './pages/analysis.js'

const routes = {
    '/': renderHomePage,
    '/login': renderLoginPage,
    '/signup': renderSignupPage,
    '/profile': renderProfilePage,
    '/logout': handleLogoutAction,
    '/editor': renderEditorPage,
    '/connections': renderConnectionsPage,
    '/admin': renderAdminPage,
    '/analysis': renderAnalysisPage
};

// 1. Navigation Function (The Engine)
window.navigateTo = (path, event) => {
    // Check if Ctrl (Windows/Linux) or Command (Mac) is pressed
    if (event && (event.ctrlKey || event.metaKey)) {
        // Allow the browser to perform the default action (open in new tab)
        // Note: For this to work perfectly, your HTML should ideally use <a> tags with hrefs
        window.open(path, '_blank');
        return;
    }

    // Standard SPA Navigation
    if (event) event.preventDefault();
    window.history.pushState({}, "", path);
    router(); 
};

// 2. Handle Logout Action
function handleLogoutAction() {
    localStorage.removeItem('user');
    window.history.replaceState({}, "", "/");
    router();
}

// 3. Central Router
async function router() {
    const path = window.location.pathname;
    updateNavbar(); 

    if (path === '/logout') {
        return handleLogoutAction();
    }

    if (path.startsWith('/editor/')) {
        const id = path.split('/')[2];
        return renderEditorPage(id);
    }

    if (path.startsWith('/details/')) {
        const id = path.split('/')[2];
        return renderDetailsPage(id); 
    }

    // This handles the static /connections route
    const view = routes[path] || renderHomePage;
    view();
}

// 4. Navbar State Management
// src/main.js
function updateNavbar() {
    const userJson = localStorage.getItem('user');
    const authLinks = document.getElementById('auth-links');
    
    if (!authLinks) return;

    if (userJson && userJson !== "undefined" && userJson !== "null") {
        try {
            const user = JSON.parse(userJson);
            const isAdmin = user.role === 'ADMIN' || user.role === 'MODERATOR';
            
            // CRITICAL CHANGE: Added 'event' to navigateTo(path, event)
            authLinks.innerHTML = `
                <div class="d-flex align-items-center gap-2">
                    ${isAdmin ? `
                        <a href="/admin" class="nav-link border border-danger rounded-pill px-3 py-1 text-danger small fw-bold" 
                           onclick="window.navigateTo('/admin', event)">
                            <i class="fas fa-user-shield me-1"></i> Admin
                        </a>` : ''}
                </div>

                <a href="/analysis" class="nav-link border border-warning rounded-pill px-3 py-1 text-warning small" 
                    onclick="window.navigateTo('/analysis', event)">
                        <i class="fas fa-brain me-1"></i> Analysis
                </a>

                <a href="/connections" class="nav-link border border-info rounded-pill px-3 py-1 text-info small me-2" 
                   onclick="window.navigateTo('/connections', event)">
                    <i class="fas fa-project-diagram me-1"></i> Graph
                </a>

                <div class="d-flex align-items-center gap-2">
                    ${isAdmin ? `
                        <a href="/editor" class="nav-link border border-primary rounded-pill px-3 py-1 text-primary small fw-bold" 
                           onclick="window.navigateTo('/editor', event)">
                            <i class="fas fa-plus-circle me-1"></i> Add Item
                        </a>` : ''}
                    
                    <a href="/profile" class="nav-link border border-secondary rounded-pill px-3 py-1 text-light small" 
                       onclick="window.navigateTo('/profile', event)">
                        <i class="fas fa-user-circle me-1"></i> Profile
                    </a>

                    <button class="btn btn-sm btn-outline-danger rounded-pill ms-2" onclick="window.navigateTo('/logout', event)">
                        <i class="fas fa-sign-out-alt"></i>
                    </button>
                </div>
            `;
        } catch (e) {
            localStorage.removeItem('user');
        }
    } else {
        authLinks.innerHTML = `
            <a href="/login" class="nav-link me-2" onclick="window.navigateTo('/login', event)">
                <i class="fas fa-sign-in-alt me-1"></i> Login
            </a>
            <a href="/signup" class="btn btn-primary rounded-pill px-4" onclick="window.navigateTo('/signup', event)">
                <i class="fas fa-user-plus me-1"></i> Sign Up
            </a>
        `;
    }
}

// 5. Global Event Listeners
window.onpopstate = router; // Handles browser back/forward buttons
document.addEventListener('DOMContentLoaded', router); // Initial load