/* ==========
   Sesión 24h
   ========== */
const AUTH_KEY = "ecualand_auth";
const AUTH_EXP_KEY = "ecualand_auth_exp";

function saveAuthHeader(header){
  const expiresAt = Date.now() + 24*60*60*1000; // 24h
  localStorage.setItem(AUTH_KEY, header);
  localStorage.setItem(AUTH_EXP_KEY, String(expiresAt));
}
function getAuthHeader(){
  const h = localStorage.getItem(AUTH_KEY);
  const exp = Number(localStorage.getItem(AUTH_EXP_KEY) || "0");
  if (!h || Date.now() > exp){
    localStorage.removeItem(AUTH_KEY);
    localStorage.removeItem(AUTH_EXP_KEY);
    return null;
  }
  return h;
}
function clearAuthHeader(){
  localStorage.removeItem(AUTH_KEY);
  localStorage.removeItem(AUTH_EXP_KEY);
}

/* ==========
   Helpers UI
   ========== */
const qs = sel => document.querySelector(sel);
const show = el => el?.classList.remove("hidden");
const hide = el => el?.classList.add("hidden");

/* ==========
   Login modal
   ========== */
const loginModal = qs("#login-modal");
const loginForm  = qs("#login-form");
const loginError = qs("#login-error");
const sessionBadge = qs("#session-badge");
const logoutBtn = qs("#logout-btn");

function updateSessionUI(){
  const has = !!getAuthHeader();
  if (sessionBadge) sessionBadge.textContent = has ? "Hola,puedes trabajar" : "Sin sesión";
  if (has) show(logoutBtn); else hide(logoutBtn);
}
function requireAuthOrShowModal(){
  const auth = getAuthHeader();
  if (!auth) {
    // Limpiar los campos al mostrar el modal de login
    if (loginForm) {
      loginForm.reset();
      // Asegura que no quede ningún valor en los inputs
      const userInput = qs('#username');
      const passInput = qs('#password');
      if (userInput) userInput.value = '';
      if (passInput) passInput.value = '';
    }
    show(loginModal);
  } else {
    hide(loginModal);
  }
  updateSessionUI();
}



loginForm?.addEventListener("submit", async (e)=>{
  e.preventDefault();
  loginError.textContent = "";

  const u = qs("#username").value.trim();
  const p = qs("#password").value.trim();
  if (!u || !p){
    loginError.textContent = "Usuario y contraseña son obligatorios.";
    return;
  }
  const authHeader = "Basic " + btoa(u + ":" + p);

  try{
    // Usamos /api/ping (GET) para validar credenciales
    const r = await fetch("/api/ping", { method:"GET", headers:{ "Authorization": authHeader }});
    console.log("Respuesta de /api/ping:", r.status);
    if (r.status === 200){
      saveAuthHeader(authHeader);
      hide(loginModal);
      updateSessionUI();
    }else if (r.status === 401){
      loginError.textContent = "Credenciales inválidas.";
    }else{
      loginError.textContent = "No se pudo verificar el acceso. Código " + r.status;
    }
  }catch(err){
    loginError.textContent = "Error de red: " + err.message;
  }
});

logoutBtn?.addEventListener("click", ()=>{
  clearAuthHeader();
  updateSessionUI();      // oculta el botón de logout y actualiza el badge
  if (loginForm) {
    loginForm.reset();
    const userInput = qs('#username');
    const passInput = qs('#password');
    if (userInput) userInput.value = '';
    if (passInput) passInput.value = '';
  }
  show(loginModal);
});


/* ==========
   Cálculo
   ========== */
const btnCalc         = qs("#btn-calc");
const btnCopy         = qs("#btn-copy");
const inputValue      = qs("#input-value");
const inputCommission = qs("#input-commission");
const inputExtra      = qs("#input-extra");
const resultTotal     = qs("#result-total");
const resultCommission= qs("#result-commission");
const errorBox        = qs("#error");

// UX: al enfocar, selecciona todo; si están vacíos al calcular, se toma 0
[inputValue, inputCommission, inputExtra].forEach(el=>{
  el?.addEventListener("focus", e=> e.target.select());
  // NUEVO: si se presiona Enter en cualquier campo, ejecuta el cálculo
  el?.addEventListener("keypress", e => {
    if (e.key === "Enter") {
      e.preventDefault();  // evita enviar el formulario por defecto
      btnCalc?.click();    // simula el clic en el botón Calcular
    }
  });
});

btnCalc?.addEventListener("click", async ()=>{
  errorBox.textContent = "";
  const value = parseFloat(inputValue.value || "0");
  const commission = parseFloat(inputCommission.value || "0");
  const extra = parseFloat(inputExtra.value || "0");

  if ([value, commission, extra].some(n => Number.isNaN(n))){
    resultTotal.textContent = "-";
    resultCommission.textContent = "-";
    errorBox.textContent = "Por favor ingrese números válidos en todos los campos.";
    return;
  }

  // --- Validación del Backend (ya corregida) ---
  // El frontend ahora puede enviar 0, y el backend (con @PositiveOrZero) lo aceptará.
  // Mantenemos la lógica de negocio de "no calcular" si es 0, lo cual está bien.
  if (value <= 0 || commission <= 0){
    resultTotal.textContent = "-";
    resultCommission.textContent = "-";
    // Tu error anterior era "No se puede calcular con valores en cero"
    // Lo cual es correcto si value o commission son 0.
    // La corrección fue para 'extra', que SÍ puede ser 0.
    // Esta lógica de frontend está bien.
    errorBox.textContent = "El valor y la comisión deben ser mayores a cero.";
    return;
  }

  const auth = getAuthHeader();
  if (!auth){ show(loginModal); return; }

  try{
    const resp = await fetch("/api/calculate", {
      method:"POST",
      headers:{
        "Content-Type":"application/json",
        "Authorization": auth
      },
      body: JSON.stringify({ value, commission, extra })
    });

    if (resp.status === 401){
      clearAuthHeader();
      show(loginModal);
      updateSessionUI();
      return;
    }
    
    // Manejo de errores de validación del backend (400)
    if (resp.status === 400) {
        const errorData = await resp.json(); // Spring Boot envía detalles
        console.error("Error de validación:", errorData);
        // Aquí podrías mostrar un error más específico si quisieras
        errorBox.textContent = "Error en los datos enviados. Revisa los campos.";
        return;
    }
    
    if (!resp.ok) throw new Error("Código " + resp.status);

    const data = await resp.json();
    resultTotal.textContent      = "$" + data.finalValue.toFixed(2);
    resultCommission.textContent = "$" + data.commissionAmount.toFixed(2);
  }catch(e){
    errorBox.textContent = "Error: " + e.message;
  }
});


btnCopy?.addEventListener("click", async ()=>{
  const t = resultTotal.textContent.replace("$","").trim();
  const c = resultCommission.textContent.replace("$","").trim();
  
  if (t === '-' || c === '-') {
    errorBox.textContent = "No hay nada que copiar.";
    return;
  }
  
  const txt = `Total a Pagar: ${t}\nComisión: ${c}`;
  try{
    await navigator.clipboard.writeText(txt);
    const orig = btnCopy.textContent;
    btnCopy.textContent = "¡Copiado!";
    setTimeout(()=> btnCopy.textContent = orig, 1500);
  }catch(_){
    errorBox.textContent = "No se pudo copiar al portapapeles.";
  }
});

/* ==========
   Inicio
   ========== */
document.addEventListener("DOMContentLoaded", () => {
    // ¡CORREGIDO! Ya no borra la sesión al cargar.
    requireAuthOrShowModal();
});