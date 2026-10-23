const API = '/api';

// Small helper for all fetch calls.
// Usage: await api('/salons')  or  await api('/salons', 'POST', { name: 'X' })
async function api(path, method = 'GET', body = null) {
  const options = { method: method, headers: {} };

  if (body) {
    options.headers['Content-Type'] = 'application/json';
    options.body = JSON.stringify(body);
  }

  const response = await fetch(API + path, options);

  // Some responses (like DELETE) have no body
  const text = await response.text();
  let data = null;
  try {
    data = text ? JSON.parse(text) : null;
  } catch (e) {
    data = null;
  }

  if (!response.ok) {
    // Our GlobalExceptionHandler sends { message: "..." }
    const msg = data && data.message ? data.message : 'Request failed (' + response.status + ')';
    throw new Error(msg);
  }

  return data;
}

// Shows a green or red box. Every page needs: <div id="message"></div>
function showMessage(text, type = 'success') {
  const box = document.getElementById('message');
  box.textContent = text;
  box.className = type; // "success" or "error"
}

// Turns "2026-10-26T11:00:00" into a readable date and time
function formatDateTime(isoString) {
  if (!isoString) return '';
  return new Date(isoString).toLocaleString();
}