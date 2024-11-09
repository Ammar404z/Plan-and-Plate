import axios from 'axios';

const api = axios.create({
  baseURL: 'http://193.196.52.222:8080',  // Directly using the IP of the VM
});

export default api;