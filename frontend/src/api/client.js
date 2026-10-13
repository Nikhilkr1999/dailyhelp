import axios from "axios";
export const api=axios.create({baseURL:import.meta.env.VITE_API_URL||"http://localhost:8080/api",withCredentials:true,headers:{"Content-Type":"application/json"}});
function cookie(name){return document.cookie.split("; ").find(row=>row.startsWith(name+"="))?.split("=").slice(1).join("=");}
api.interceptors.request.use(config=>{const token=cookie("XSRF-TOKEN");if(token&&!['get','head','options'].includes((config.method||'get').toLowerCase()))config.headers["X-XSRF-TOKEN"]=decodeURIComponent(token);return config;});
export async function initializeCsrf(){await api.get('/csrf');}
export function messageFromError(error){return error?.response?.data?.message|| (error?.response?.status===401?'Please sign in to continue.':error?.message==='Network Error'?'Cannot reach the server. Check that the backend is running.':'Something went wrong. Please try again.');}
