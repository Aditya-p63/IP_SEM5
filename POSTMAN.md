# Salon Queue: API Endpoints

Base URL: `http://localhost:8080/api`

## Salons
| Method | URL | Body |
|--------|-----|------|
| POST | /salons | `{"name":"Glamour Studio","address":"Main St","phone":"9999999999","openTime":"09:00","closeTime":"20:00"}` |
| GET | /salons | |
| GET | /salons/{id} | |
| PUT | /salons/{id} | same as POST |
| DELETE | /salons/{id} | |

## Services
| Method | URL | Body |
|--------|-----|------|
| POST | /salons/{salonId}/services | `{"name":"Haircut","durationMinutes":30,"price":300}` |
| GET | /salons/{salonId}/services | |
| GET | /services/{id} | |
| PUT | /services/{id} | same as POST |
| DELETE | /services/{id} | |

## Staff
| Method | URL | Body |
|--------|-----|------|
| POST | /salons/{salonId}/staff | `{"name":"Riya","specialization":"Hair","active":true}` |
| GET | /salons/{salonId}/staff | |
| GET | /staff/{id} | |
| PUT | /staff/{id} | same as POST |
| DELETE | /staff/{id} | |

## Appointments
| Method | URL | Body |
|--------|-----|------|
| POST | /appointments | `{"customerName":"Asha","customerPhone":"9876543210","serviceId":1,"staffId":1,"startTime":"2026-10-26T11:00:00"}` |
| GET | /salons/{salonId}/appointments?date=2026-10-26 | |
| GET | /appointments/{id} | |
| PUT | /appointments/{id} | same as POST |
| PATCH | /appointments/{id}/cancel | |
| DELETE | /appointments/{id} | |
| GET | /salons/{salonId}/available-slots?serviceId=1&staffId=1&date=2026-10-26 | |

## Queue
| Method | URL | Body |
|--------|-----|------|
| POST | /salons/{salonId}/queue | `{"customerName":"Ravi","customerPhone":"9123456780","serviceId":1}` |
| GET | /salons/{salonId}/queue | |
| GET | /salons/{salonId}/queue?status=WAITING | |
| GET | /queue/{id} | |
| PUT | /queue/{id} | same as POST |
| DELETE | /queue/{id} | |
| GET | /queue/{id}/status | |
| PATCH | /queue/{id}/status?status=IN_SERVICE&staffId=1 | |
| PATCH | /queue/{id}/status?status=DONE | |
| PATCH | /queue/{id}/status?status=LEFT | |

Valid queue transitions: WAITING to IN_SERVICE, WAITING to LEFT, IN_SERVICE to DONE.

## Error format
```json
{ "timestamp": "...", "status": 404, "error": "Not Found", "message": "..." }
```
Codes: 400 invalid request, 404 not found, 409 conflict.