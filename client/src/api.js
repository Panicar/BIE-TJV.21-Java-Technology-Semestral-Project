/**
 * Epistemic Harmony API Service
 * Base URL: http://localhost:8081
 */

const BASE_URL = 'http://localhost:8081';

const handleResponse = async (response) => {
  if (!response.ok) {
    const error = await response.json().catch(() => ({ message: response.statusText }));
    throw new Error(error.message || 'API request failed');
  }
  if (response.status === 204 || response.headers.get('content-length') === '0') return null;
  return response.json();
};

const EpistemicHarmonyAPI = {
  // --- USER OPERATIONS ---
  users: {
    getAll: () => fetch(`${BASE_URL}/api/users`).then(handleResponse),
    
    getById: (id) => fetch(`${BASE_URL}/api/users/${id}`).then(handleResponse),
    
    getByUsername: (username) => fetch(`${BASE_URL}/api/users/username/${username}`).then(handleResponse),
    
    getByEmail: (email) => fetch(`${BASE_URL}/api/users/email/${email}`).then(handleResponse),
    
    getByRole: (role) => fetch(`${BASE_URL}/api/users/role/${role}`).then(handleResponse),
    
    create: (userData) => fetch(`${BASE_URL}/api/users`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(userData)
    }).then(handleResponse),
    
    update: (id, userData) => fetch(`${BASE_URL}/api/users/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(userData)
    }).then(handleResponse),
    
    delete: (id) => fetch(`${BASE_URL}/api/users/${id}`, { method: 'DELETE' }).then(handleResponse)
  },

  // --- REVIEW OPERATIONS ---
  reviews: {
    getAll: () => fetch(`${BASE_URL}/api/reviews`).then(handleResponse),
    
    getById: (id) => fetch(`${BASE_URL}/api/reviews/${id}`).then(handleResponse),
    
    getByUser: (userId) => fetch(`${BASE_URL}/api/reviews/user/${userId}`).then(handleResponse),
    
    getByItem: (itemId) => fetch(`${BASE_URL}/api/reviews/item/${itemId}`).then(handleResponse),
    
    getAverageRating: (itemId) => fetch(`${BASE_URL}/api/reviews/item/${itemId}/average-rating`).then(handleResponse),
    
    countForItem: (itemId) => fetch(`${BASE_URL}/api/reviews/item/${itemId}/count`).then(handleResponse),
    
    checkExists: (userId, itemId) => fetch(`${BASE_URL}/api/reviews/user/${userId}/item/${itemId}/exists`).then(handleResponse),
    
    create: (reviewData) => fetch(`${BASE_URL}/api/reviews`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(reviewData)
    }).then(handleResponse),

    update: (id, data) => fetch(`${BASE_URL}/api/reviews/${id}`, { 
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    }).then(async res => {
        if (!res.ok) {
            const errorBody = await res.text();
            console.error(`Backend Error (${res.status}):`, errorBody);
            throw new Error(`Update failed with status ${res.status}`);
        }
        return res.json();
    }),

    delete: (id) => fetch(`${BASE_URL}/api/reviews/${id}`, { method: 'DELETE' }).then(handleResponse)
  },

  // --- EPISTEMIC ITEM OPERATIONS ---
  items: {
    getAll: () => fetch(`${BASE_URL}/api/epistemic-items`).then(handleResponse),
    
    getById: (id) => fetch(`${BASE_URL}/api/epistemic-items/${id}`).then(handleResponse),
    
    getByType: (type) => fetch(`${BASE_URL}/api/epistemic-items/type/${type}`).then(handleResponse), // THEORY or STATEMENT
    

    getByCategory: (category) => 
      fetch(`${BASE_URL}/api/epistemic-items/category?category=${encodeURIComponent(category)}`)
          .then(handleResponse),


    search: (name) => fetch(`${BASE_URL}/api/epistemic-items/search?name=${encodeURIComponent(name)}`).then(handleResponse),
    
    create: (itemData) => fetch(`${BASE_URL}/api/epistemic-items`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(itemData)
    }).then(handleResponse),

    update: (id, itemData) => fetch(`${BASE_URL}/api/epistemic-items/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(itemData)
    }).then(handleResponse),

    delete: (id) => fetch(`${BASE_URL}/api/epistemic-items/${id}`, { method: 'DELETE' }).then(handleResponse)
  },

  // --- CONNECTION OPERATIONS ---
  connections: {
    getAll: () => fetch(`${BASE_URL}/api/connections`).then(handleResponse),
    
    getById: (id) => fetch(`${BASE_URL}/api/connections/${id}`).then(handleResponse),
    
    getByType: (type) => fetch(`${BASE_URL}/api/connections/type/${type}`).then(handleResponse),
    
    getFromItem: (itemId) => fetch(`${BASE_URL}/api/connections/from/${itemId}`).then(handleResponse),
    
    getToItem: (itemId) => fetch(`${BASE_URL}/api/connections/to/${itemId}`).then(handleResponse),
    
    getStrong: (minStrength) => fetch(`${BASE_URL}/api/connections/strong?minStrength=${minStrength}`).then(handleResponse),
    
    create: (connectionData) => fetch(`${BASE_URL}/api/connections`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(connectionData)
    }).then(handleResponse),

    update: (id, connectionData) => fetch(`${BASE_URL}/api/connections/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(connectionData)
    }).then(handleResponse),

    delete: (id) => fetch(`${BASE_URL}/api/connections/${id}`, { 
      method: 'DELETE' 
    }).then(handleResponse)
  }
};

export default EpistemicHarmonyAPI;