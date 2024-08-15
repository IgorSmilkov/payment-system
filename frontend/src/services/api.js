import axios from 'axios';

const api = axios.create({
  baseURL: 'https://localhost:8443',
  headers: {
    'Content-Type': 'application/json',
  },
});

export default api;
