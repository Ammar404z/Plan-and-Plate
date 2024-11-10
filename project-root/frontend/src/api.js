import axios from 'axios';

const api = axios.create({
  baseURL: 'http://[2001:7c0:2320:1:f816:3eff:fe09:d4aa]:8080',  // IPv6 address with port
});

export default api;