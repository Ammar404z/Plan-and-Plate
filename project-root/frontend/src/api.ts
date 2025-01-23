/*
import axios from "axios";

const api = axios.create({
  baseURL: "http://[2001:7c0:2320:1:f816:3eff:fe09:d4aa]:8080", // IPv6 address with port
});

export default api;
*/

import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080", // for Local testing purposes, when finished change back to the Ipv6 of the vm, so that it can be accessed from the browser. Dont forget to configure the Database for the VM
});

export default api;
