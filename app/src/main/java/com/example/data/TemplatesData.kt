package com.example.data

data class ProjectTemplate(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val prompt: String,
    val htmlContent: String,
    val cssContent: String,
    val jsContent: String,
    val files: Map<String, String> = emptyMap()
)

object TemplatesData {
    val templates = listOf(
        ProjectTemplate(
            id = "portfolio",
            title = "Creative Portfolio",
            description = "Showcase your best work with dark aesthetic and smooth interactive cards.",
            category = "CREATIVE",
            prompt = "Create a premium dark-themed developer portfolio with project gallery, skills, and contact section.",
            htmlContent = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1.0">
                  <title>Alex Rivera — Senior Creative Engineer</title>
                  <link rel="stylesheet" href="styles.css">
                </head>
                <body>
                  <nav class="nav">
                    <div class="logo">AR<span>.</span></div>
                    <div class="links">
                      <a href="#work">Work</a>
                      <a href="#skills">Skills</a>
                      <button class="btn-hire" onclick="sayHello()">Contact</button>
                    </div>
                  </nav>

                  <header class="hero">
                    <div class="badge">AVAILABLE FOR Q4 PROJECTS</div>
                    <h1>Designing Digital <span class="gradient">Experiences</span> That Matter</h1>
                    <p class="subtitle">Full-stack creative engineer building high-impact products with Next.js, WebGL & AI systems.</p>
                    <div class="cta-group">
                      <button class="primary-btn" onclick="scrollToWork()">Explore Projects</button>
                      <button class="secondary-btn" onclick="viewResume()">View Resume</button>
                    </div>
                  </header>

                  <section id="work" class="projects-section">
                    <h2>Selected Projects</h2>
                    <div class="grid">
                      <div class="card" onclick="alertProject('Neural Canvas')">
                        <div class="card-tag">GEN AI STUDIO</div>
                        <h3>Neural Canvas</h3>
                        <p>Real-time collaborative generative canvas with diffusion models.</p>
                        <span class="stars">★★★★★ 99.4% rating</span>
                      </div>
                      <div class="card" onclick="alertProject('Aura Analytics')">
                        <div class="card-tag">FINTECH PLATFORM</div>
                        <h3>Aura Analytics</h3>
                        <p>High-frequency telemetry dashboard processing 12M events/sec.</p>
                        <span class="stars">★★★★★ Enterprise</span>
                      </div>
                      <div class="card" onclick="alertProject('Voxel OS')">
                        <div class="card-tag">SPATIAL COMPUTING</div>
                        <h3>Voxel OS</h3>
                        <p>WebXR operating system prototype with gesture tracking.</p>
                        <span class="stars">★★★★★ Open Source</span>
                      </div>
                    </div>
                  </section>

                  <section id="skills" class="skills-section">
                    <h2>Core Arsenal</h2>
                    <div class="tags">
                      <span>Kotlin & Jetpack Compose</span>
                      <span>TypeScript</span>
                      <span>WebGL / Three.js</span>
                      <span>LLM Fine-tuning</span>
                      <span>Rust</span>
                      <span>System Architecture</span>
                    </div>
                  </section>

                  <footer class="footer">
                    <p>© 2026 Alex Rivera. Built inside Hazon AI Mobile Studio.</p>
                  </footer>

                  <script src="app.js"></script>
                </body>
                </html>
            """.trimIndent(),
            cssContent = """
                * { box-sizing: border-box; margin: 0; padding: 0; }
                body {
                  background: #090911;
                  color: #f5f3ff;
                  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
                  line-height: 1.6;
                  padding: 16px;
                }
                .nav {
                  display: flex;
                  justify-content: space-between;
                  align-items: center;
                  padding: 16px 0;
                  border-bottom: 1px solid rgba(180, 170, 255, 0.12);
                }
                .logo { font-size: 22px; font-weight: 800; }
                .logo span { color: #8b5cf6; }
                .links { display: flex; align-items: center; gap: 16px; }
                .links a { color: #aaa8bf; text-decoration: none; font-size: 14px; }
                .btn-hire {
                  background: #7851dd;
                  color: white;
                  border: none;
                  padding: 8px 16px;
                  border-radius: 8px;
                  font-size: 13px;
                  font-weight: 600;
                  cursor: pointer;
                }
                .hero { text-align: center; padding: 48px 12px; }
                .badge {
                  display: inline-block;
                  font-size: 11px;
                  font-weight: 700;
                  color: #c4b5fd;
                  background: rgba(139, 92, 246, 0.15);
                  padding: 6px 14px;
                  border-radius: 20px;
                  border: 1px solid rgba(167, 139, 250, 0.3);
                  margin-bottom: 20px;
                }
                h1 { font-size: 32px; font-weight: 800; letter-spacing: -1px; line-height: 1.2; margin-bottom: 16px; }
                .gradient {
                  background: linear-gradient(135deg, #c4b5fd, #8b5cf6, #60a5fa);
                  -webkit-background-clip: text;
                  -webkit-text-fill-color: transparent;
                }
                .subtitle { color: #9694ad; font-size: 15px; max-width: 500px; margin: 0 auto 24px; }
                .cta-group { display: flex; justify-content: center; gap: 12px; }
                .primary-btn {
                  background: linear-gradient(110deg, #8859ef, #5e67e9);
                  color: white;
                  border: none;
                  padding: 12px 24px;
                  border-radius: 10px;
                  font-weight: 600;
                  cursor: pointer;
                  box-shadow: 0 4px 15px rgba(124, 58, 237, 0.3);
                }
                .secondary-btn {
                  background: #151525;
                  color: #c4b5fd;
                  border: 1px solid rgba(180, 170, 255, 0.2);
                  padding: 12px 20px;
                  border-radius: 10px;
                  cursor: pointer;
                }
                .projects-section { margin-top: 36px; }
                h2 { font-size: 20px; margin-bottom: 18px; color: #e9e5ff; }
                .grid { display: flex; flex-direction: column; gap: 14px; }
                .card {
                  background: #11111e;
                  border: 1px solid rgba(180, 170, 255, 0.12);
                  border-radius: 12px;
                  padding: 20px;
                  cursor: pointer;
                  transition: transform 0.2s, border-color 0.2s;
                }
                .card:hover { transform: translateY(-2px); border-color: #8b5cf6; }
                .card-tag { font-size: 10px; font-weight: 700; color: #a78bfa; letter-spacing: 1px; margin-bottom: 6px; }
                .card h3 { font-size: 17px; margin-bottom: 6px; color: #fff; }
                .card p { font-size: 13px; color: #9694ad; margin-bottom: 10px; }
                .stars { font-size: 11px; color: #54dba4; }
                .skills-section { margin-top: 40px; }
                .tags { display: flex; flex-wrap: wrap; gap: 8px; }
                .tags span {
                  background: #151525;
                  color: #d1cdde;
                  font-size: 12px;
                  padding: 8px 14px;
                  border-radius: 8px;
                  border: 1px solid rgba(180, 170, 255, 0.1);
                }
                .footer { text-align: center; margin-top: 48px; padding-top: 24px; border-top: 1px solid rgba(180, 170, 255, 0.1); color: #68667c; font-size: 11px; }
            """.trimIndent(),
            jsContent = """
                console.log("Portfolio loaded successfully on Hazon AI.");
                function sayHello() {
                  alert("Alex Rivera is happy to connect! Email: alex@studio-hazon.dev");
                }
                function scrollToWork() {
                  document.getElementById("work").scrollIntoView({ behavior: "smooth" });
                }
                function viewResume() {
                  alert("Opening Alex Rivera Resume (PDF format)...");
                }
                function alertProject(name) {
                  alert("Viewing detailed case study for: " + name);
                }
            """.trimIndent()
        ),
        ProjectTemplate(
            id = "dashboard",
            title = "SaaS Analytics Dashboard",
            description = "Metric cards, live telemetry charts, and recent activity monitoring.",
            category = "PRODUCTIVITY",
            prompt = "Create a modern SaaS analytics dashboard with metric cards, charts, recent activity, and responsive layout.",
            htmlContent = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1.0">
                  <title>PulseMetrics — Telemetry Cloud</title>
                  <link rel="stylesheet" href="styles.css">
                </head>
                <body>
                  <div class="dash-container">
                    <header class="dash-header">
                      <div class="brand">⚡ PulseMetrics</div>
                      <div class="user-pill">
                        <span class="live-dot"></span> LIVE TELEMETRY
                      </div>
                    </header>

                    <div class="stats-row">
                      <div class="stat-card">
                        <span class="label">Total Requests</span>
                        <div class="val">4,892,104</div>
                        <span class="change up">↑ 24.8% vs last week</span>
                      </div>
                      <div class="stat-card">
                        <span class="label">Avg Latency</span>
                        <div class="val">18.4ms</div>
                        <span class="change up">↑ 12% faster</span>
                      </div>
                      <div class="stat-card">
                        <span class="label">Active Containers</span>
                        <div class="val">64 / 64</div>
                        <span class="change neutral">100% healthy</span>
                      </div>
                    </div>

                    <div class="chart-card">
                      <div class="chart-header">
                        <h3>Traffic Throughput (req/s)</h3>
                        <button onclick="refreshChart()">Refresh</button>
                      </div>
                      <div class="bar-chart" id="barChart">
                        <!-- Rendered by JS -->
                      </div>
                    </div>

                    <div class="activity-card">
                      <h3>Cluster Operations Log</h3>
                      <ul id="logList">
                        <li><span class="time">10:42:15</span> Auto-scaled pod worker-08 (CPU 84%)</li>
                        <li><span class="time">10:41:02</span> Healthcheck passed for region us-east4</li>
                        <li><span class="time">10:39:50</span> Certificate renewed (*.hazon.app)</li>
                      </ul>
                      <button class="add-log-btn" onclick="addSampleLog()">Simulate Event</button>
                    </div>
                  </div>
                  <script src="app.js"></script>
                </body>
                </html>
            """.trimIndent(),
            cssContent = """
                * { box-sizing: border-box; margin: 0; padding: 0; }
                body {
                  background: #090911;
                  color: #f5f3ff;
                  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
                  padding: 16px;
                }
                .dash-container { display: flex; flex-direction: column; gap: 16px; }
                .dash-header { display: flex; justify-content: space-between; align-items: center; padding-bottom: 8px; border-bottom: 1px solid rgba(180, 170, 255, 0.1); }
                .brand { font-size: 18px; font-weight: 800; color: #a78bfa; }
                .user-pill { font-size: 10px; font-weight: 700; background: #151525; padding: 6px 12px; border-radius: 20px; border: 1px solid rgba(84, 219, 164, 0.4); display: flex; align-items: center; gap: 6px; }
                .live-dot { width: 8px; height: 8px; background: #54dba4; border-radius: 50%; box-shadow: 0 0 8px #54dba4; }
                .stats-row { display: grid; grid-template-columns: repeat(auto-fit, minmax(130px, 1fr)); gap: 12px; }
                .stat-card { background: #10101c; border: 1px solid rgba(180, 170, 255, 0.12); border-radius: 12px; padding: 14px; }
                .label { font-size: 11px; color: #9694ad; }
                .val { font-size: 20px; font-weight: 800; margin: 6px 0; color: #fff; }
                .change { font-size: 10px; font-weight: 600; }
                .change.up { color: #54dba4; }
                .change.neutral { color: #60a5fa; }
                .chart-card { background: #10101c; border: 1px solid rgba(180, 170, 255, 0.12); border-radius: 12px; padding: 16px; }
                .chart-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
                .chart-header h3 { font-size: 14px; color: #ded5ff; }
                .chart-header button { background: #1b1b30; color: #c4b5fd; border: 1px solid rgba(180,170,255,0.2); padding: 4px 10px; border-radius: 6px; font-size: 11px; cursor: pointer; }
                .bar-chart { display: flex; align-items: flex-end; justify-content: space-between; height: 120px; gap: 6px; padding: 10px 0; }
                .bar-col { flex: 1; display: flex; flex-direction: column; align-items: center; height: 100%; justify-content: flex-end; }
                .bar { width: 100%; background: linear-gradient(180deg, #8b5cf6, #3b82f6); border-radius: 4px 4px 0 0; transition: height 0.4s ease; min-height: 8px; }
                .bar-label { font-size: 9px; color: #77758e; margin-top: 6px; }
                .activity-card { background: #10101c; border: 1px solid rgba(180, 170, 255, 0.12); border-radius: 12px; padding: 16px; }
                .activity-card h3 { font-size: 14px; margin-bottom: 12px; color: #ded5ff; }
                ul { list-style: none; display: flex; flex-direction: column; gap: 8px; font-size: 12px; color: #aaa8bf; }
                .time { color: #8b5cf6; font-family: monospace; font-size: 11px; margin-right: 6px; }
                .add-log-btn { margin-top: 14px; width: 100%; background: #151525; color: #c4b5fd; border: 1px solid rgba(180, 170, 255, 0.2); padding: 8px; border-radius: 8px; font-size: 12px; cursor: pointer; }
            """.trimIndent(),
            jsContent = """
                const barChart = document.getElementById("barChart");
                const hours = ["06h", "08h", "10h", "12h", "14h", "16h", "18h", "20h"];

                function renderChart() {
                  barChart.innerHTML = "";
                  hours.forEach(hour => {
                    const pct = Math.floor(Math.random() * 65) + 30;
                    const col = document.createElement("div");
                    col.className = "bar-col";
                    col.innerHTML = '<div class="bar" style="height: ' + pct + '%"></div><span class="bar-label">' + hour + '</span>';
                    barChart.appendChild(col);
                  });
                }

                function refreshChart() {
                  renderChart();
                }

                function addSampleLog() {
                  const list = document.getElementById("logList");
                  const now = new Date().toTimeString().split(" ")[0];
                  const li = document.createElement("li");
                  li.innerHTML = '<span class="time">' + now + '</span> Ingested packet burst (4,210 RPS)';
                  list.prepend(li);
                }

                renderChart();
            """.trimIndent()
        ),
        ProjectTemplate(
            id = "ai-landing",
            title = "AI Landing Page",
            description = "Futuristic AI product page with gradient effects, feature cards, and interactive demo.",
            category = "NEXT-GEN AI",
            prompt = "Create a futuristic AI product landing page with gradient effects, feature cards, pricing, and call-to-action buttons.",
            htmlContent = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1.0">
                  <title>Synapse AI — Autonomous Intelligence</title>
                  <link rel="stylesheet" href="styles.css">
                </head>
                <body>
                  <div class="glow-orb"></div>
                  <nav>
                    <div class="logo">⚡ Synapse.ai</div>
                    <button class="try-btn" onclick="alert('Starting free trial!')">Get Started</button>
                  </nav>

                  <section class="hero">
                    <span class="tag">NEXT-GEN REASONING ENGINE 3.5</span>
                    <h1>Think Faster Than Human <span class="shimmer">Intuition</span></h1>
                    <p>Execute multi-step reasoning, autonomous code generation, and complex software builds in milliseconds.</p>
                    <div class="action-box">
                      <input type="text" id="aiInput" placeholder="Ask Synapse anything..." value="Refactor microservices architecture for latency">
                      <button onclick="runAi()">Generate</button>
                    </div>
                    <div id="aiOutput" class="ai-output hidden"></div>
                  </section>

                  <section class="features">
                    <div class="feat">
                      <h3>🧠 Reasoning Core</h3>
                      <p>Deep-tree search exploration with self-correcting verification steps.</p>
                    </div>
                    <div class="feat">
                      <h3>⚡ Instant Compilation</h3>
                      <p>Full WASM and bytecode synthesis running in 4.2ms.</p>
                    </div>
                    <div class="feat">
                      <h3>🛡️ Zero-Knowledge</h3>
                      <p>Client-side cryptographic data enclave guaranteeing model confidentiality.</p>
                    </div>
                  </section>
                  <script src="app.js"></script>
                </body>
                </html>
            """.trimIndent(),
            cssContent = """
                * { box-sizing: border-box; margin: 0; padding: 0; }
                body {
                  background: #090911;
                  color: #f5f3ff;
                  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
                  padding: 20px;
                  position: relative;
                  overflow-x: hidden;
                }
                .glow-orb {
                  position: absolute;
                  top: -80px;
                  left: 50%;
                  transform: translateX(-50%);
                  width: 320px;
                  height: 320px;
                  background: radial-gradient(circle, rgba(139, 92, 246, 0.28) 0%, transparent 70%);
                  pointer-events: none;
                }
                nav { display: flex; justify-content: space-between; align-items: center; padding-bottom: 24px; }
                .logo { font-size: 20px; font-weight: 800; color: #c4b5fd; }
                .try-btn { background: #7851dd; color: white; border: none; padding: 8px 16px; border-radius: 8px; font-weight: 600; cursor: pointer; }
                .hero { text-align: center; padding: 36px 0; }
                .tag { font-size: 10px; font-weight: 700; color: #a78bfa; background: rgba(139, 92, 246, 0.15); border: 1px solid rgba(167, 139, 250, 0.3); padding: 5px 12px; border-radius: 20px; }
                h1 { font-size: 30px; font-weight: 800; margin: 18px 0 12px; line-height: 1.2; }
                .shimmer {
                  background: linear-gradient(90deg, #c4b5fd, #67e8f9, #a78bfa);
                  -webkit-background-clip: text;
                  -webkit-text-fill-color: transparent;
                }
                p { color: #9694ad; font-size: 14px; max-width: 480px; margin: 0 auto 24px; }
                .action-box { display: flex; max-width: 480px; margin: 0 auto; background: #151525; border: 1px solid rgba(167, 139, 250, 0.3); border-radius: 12px; padding: 6px; }
                .action-box input { flex: 1; background: transparent; border: none; color: #fff; padding: 8px 12px; font-size: 13px; outline: none; }
                .action-box button { background: #8b5cf6; color: white; border: none; padding: 8px 18px; border-radius: 8px; font-weight: 600; cursor: pointer; }
                .ai-output { margin: 20px auto 0; max-width: 480px; background: #11111e; border: 1px solid #8b5cf6; border-radius: 10px; padding: 14px; font-size: 13px; text-align: left; color: #d1cdde; }
                .hidden { display: none; }
                .features { display: flex; flex-direction: column; gap: 14px; margin-top: 40px; }
                .feat { background: #10101c; border: 1px solid rgba(180, 170, 255, 0.1); border-radius: 12px; padding: 18px; }
                .feat h3 { font-size: 16px; margin-bottom: 6px; color: #f5f3ff; }
                .feat p { margin: 0; font-size: 13px; }
            """.trimIndent(),
            jsContent = """
                function runAi() {
                  const input = document.getElementById("aiInput").value;
                  const output = document.getElementById("aiOutput");
                  output.classList.remove("hidden");
                  output.innerHTML = "Thinking...";
                  setTimeout(() => {
                    output.innerHTML = "<strong>Synapse AI Output:</strong><br/>Analysis completed in 18ms.<br/>• Identified 4 bottleneck endpoints in auth-gateway.<br/>• Proposed gRPC stream pooling reducing tail latency by 43%.";
                  }, 600);
                }
            """.trimIndent()
        ),
        ProjectTemplate(
            id = "store",
            title = "Online Store",
            description = "Sleek online storefront with product cards, category filter, and dynamic shopping cart.",
            category = "COMMERCE",
            prompt = "Create a sleek online store with responsive product grid, category filters, product details, and shopping cart.",
            htmlContent = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1.0">
                  <title>Verve — Minimalist Gear</title>
                  <link rel="stylesheet" href="styles.css">
                </head>
                <body>
                  <header>
                    <div class="logo">VERVE STUDIO</div>
                    <button class="cart-btn" onclick="toggleCart()">Cart (<span id="cartCount">0</span>)</button>
                  </header>

                  <div class="filter-pills">
                    <button class="active" onclick="filterCategory('all', this)">All Items</button>
                    <button onclick="filterCategory('tech', this)">Workspace</button>
                    <button onclick="filterCategory('apparel', this)">Apparel</button>
                  </div>

                  <div class="products-grid" id="productGrid">
                    <!-- Loaded by JS -->
                  </div>

                  <div id="cartModal" class="cart-modal hidden">
                    <div class="cart-content">
                      <h3>Your Cart</h3>
                      <div id="cartItems">Your cart is empty.</div>
                      <div class="total-row">
                        <strong>Total:</strong>
                        <span id="cartTotal">$0.00</span>
                      </div>
                      <button class="checkout-btn" onclick="checkout()">Checkout</button>
                      <button class="close-btn" onclick="toggleCart()">Close</button>
                    </div>
                  </div>
                  <script src="app.js"></script>
                </body>
                </html>
            """.trimIndent(),
            cssContent = """
                * { box-sizing: border-box; margin: 0; padding: 0; }
                body { background: #090911; color: #f5f3ff; font-family: -apple-system, sans-serif; padding: 16px; }
                header { display: flex; justify-content: space-between; align-items: center; padding-bottom: 16px; border-bottom: 1px solid rgba(180,170,255,0.1); }
                .logo { font-size: 18px; font-weight: 800; letter-spacing: 2px; color: #c4b5fd; }
                .cart-btn { background: #8b5cf6; color: white; border: none; padding: 8px 14px; border-radius: 8px; font-weight: 600; cursor: pointer; }
                .filter-pills { display: flex; gap: 8px; margin: 18px 0; }
                .filter-pills button { background: #151525; color: #aaa8bf; border: 1px solid rgba(180,170,255,0.1); padding: 6px 14px; border-radius: 20px; font-size: 12px; cursor: pointer; }
                .filter-pills button.active { background: #8b5cf6; color: white; border-color: #8b5cf6; }
                .products-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(140px, 1fr)); gap: 14px; }
                .p-card { background: #11111e; border: 1px solid rgba(180,170,255,0.12); border-radius: 12px; padding: 14px; display: flex; flex-direction: column; justify-content: space-between; }
                .p-emoji { font-size: 38px; text-align: center; margin: 10px 0; }
                .p-title { font-size: 14px; font-weight: 700; margin-bottom: 4px; }
                .p-price { font-size: 14px; color: #54dba4; font-weight: 700; margin-bottom: 10px; }
                .add-btn { background: #1b1b30; color: #c4b5fd; border: 1px solid rgba(180,170,255,0.2); padding: 8px; border-radius: 8px; font-size: 12px; font-weight: 600; cursor: pointer; }
                .add-btn:hover { background: #8b5cf6; color: white; }
                .cart-modal { position: fixed; inset: 0; background: rgba(0,0,0,0.7); display: flex; align-items: center; justify-content: center; z-index: 100; }
                .cart-content { background: #151525; border: 1px solid rgba(180,170,255,0.2); border-radius: 16px; padding: 20px; width: 85%; max-width: 340px; }
                .cart-content h3 { font-size: 18px; margin-bottom: 12px; }
                .total-row { display: flex; justify-content: space-between; margin: 16px 0; font-size: 16px; border-top: 1px solid rgba(180,170,255,0.1); padding-top: 10px; }
                .checkout-btn { width: 100%; background: #54dba4; color: #090911; border: none; padding: 10px; border-radius: 8px; font-weight: 700; cursor: pointer; margin-bottom: 8px; }
                .close-btn { width: 100%; background: transparent; color: #aaa8bf; border: 1px solid rgba(180,170,255,0.1); padding: 8px; border-radius: 8px; cursor: pointer; }
                .hidden { display: none; }
            """.trimIndent(),
            jsContent = """
                const products = [
                  { id: 1, title: "Cyber Keyboard", price: 189, category: "tech", emoji: "⌨️" },
                  { id: 2, title: "Precision Mouse", price: 95, category: "tech", emoji: "🖱️" },
                  { id: 3, title: "Merino Hoodie", price: 120, category: "apparel", emoji: "🧥" },
                  { id: 4, title: "Acoustic ANC Buds", price: 240, category: "tech", emoji: "🎧" }
                ];

                let cart = [];

                function renderProducts(cat = "all") {
                  const grid = document.getElementById("productGrid");
                  grid.innerHTML = "";
                  const filtered = cat === "all" ? products : products.filter(p => p.category === cat);
                  filtered.forEach(p => {
                    const card = document.createElement("div");
                    card.className = "p-card";
                    card.innerHTML = `
                      <div class="p-emoji">${'$'}{p.emoji}</div>
                      <div class="p-title">${'$'}{p.title}</div>
                      <div class="p-price">${'$'}${'$'}{p.price}</div>
                      <button class="add-btn" onclick="addToCart(${'$'}{p.id})">Add to Bag</button>
                    `;
                    grid.appendChild(card);
                  });
                }

                function addToCart(id) {
                  const prod = products.find(p => p.id === id);
                  if (prod) {
                    cart.push(prod);
                    updateCartUi();
                  }
                }

                function updateCartUi() {
                  document.getElementById("cartCount").textContent = cart.length;
                  const itemsDiv = document.getElementById("cartItems");
                  if (cart.length === 0) {
                    itemsDiv.innerHTML = "Your cart is empty.";
                    document.getElementById("cartTotal").textContent = "${'$'}0.00";
                    return;
                  }
                  itemsDiv.innerHTML = cart.map(item => `<div>${'$'}{item.emoji} ${'$'}{item.title} — ${'$'}${'$'}{item.price}</div>`).join("");
                  const total = cart.reduce((acc, curr) => acc + curr.price, 0);
                  document.getElementById("cartTotal").textContent = "${'$'}" + total.toFixed(2);
                }

                function toggleCart() {
                  document.getElementById("cartModal").classList.toggle("hidden");
                }

                function filterCategory(cat, btn) {
                  document.querySelectorAll(".filter-pills button").forEach(b => b.classList.remove("active"));
                  btn.classList.add("active");
                  renderProducts(cat);
                }

                function checkout() {
                  alert("Order placed successfully! Total: " + document.getElementById("cartTotal").textContent);
                  cart = [];
                  updateCartUi();
                  toggleCart();
                }

                renderProducts();
            """.trimIndent()
        )
    )
}
