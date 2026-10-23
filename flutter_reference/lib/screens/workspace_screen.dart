import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:flutter_highlight/flutter_highlight.dart';
import 'package:flutter_highlight/themes/atom-one-dark.dart';
import 'package:webview_flutter/webview_flutter.dart';
import '../services/api_service.dart';

class WorkspaceScreen extends StatefulWidget {
  const WorkspaceScreen({super.key});

  @override
  State<WorkspaceScreen> createState() => _WorkspaceScreenState();
}

class _WorkspaceScreenState extends State<WorkspaceScreen> with SingleTickerProviderStateMixin {
  late TabController _tabController;
  late final WebViewController _webViewController;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 3, vsync: this);
    _webViewController = WebViewController()
      ..setJavaScriptMode(JavaScriptMode.unrestricted);
  }

  void _loadPreviewHtml(HazonProvider provider) {
    final files = provider.currentProject?.files ?? {};
    final html = files['index.html'] ?? '<html><body>No html</body></html>';
    final css = files['styles.css'] ?? '';
    final js = files['app.js'] ?? '';

    final compiled = html
        .replaceFirst('</head>', '<style>$css</style></head>')
        .replaceFirst('</body>', '<script>$js</script></body>');

    _webViewController.loadHtmlString(compiled);
  }

  @override
  Widget build(BuildContext context) {
    final provider = Provider.of<HazonProvider>(context);

    return Scaffold(
      appBar: AppBar(
        title: Text(provider.currentProject?.title ?? 'Workspace'),
        backgroundColor: const Color(0xFF10101C),
        bottom: TabBar(
          controller: _tabController,
          indicatorColor: const Color(0xFF8B5CF6),
          labelColor: const Color(0xFFC4B5FD),
          unselectedLabelColor: Colors.grey,
          onTap: (index) {
            if (index == 2) _loadPreviewHtml(provider);
          },
          tabs: const [
            Tab(icon: Icon(Icons.chat_bubble_outline), text: 'Discussion'),
            Tab(icon: Icon(Icons.folder_open), text: 'Files'),
            Tab(icon: Icon(Icons.monitor), text: 'Preview'),
          ],
        ),
      ),
      body: TabBarView(
        controller: _tabController,
        children: [
          // 1. Discussion Tab
          ListView.builder(
            padding: const EdgeInsets.all(16),
            itemCount: provider.currentMessages.length,
            itemBuilder: (context, i) {
              final msg = provider.currentMessages[i];
              return Align(
                alignment: msg.role == 'user' ? Alignment.centerRight : Alignment.centerLeft,
                child: Container(
                  margin: const EdgeInsets.symmetric(vertical: 6),
                  padding: const EdgeInsets.all(12),
                  decoration: BoxDecoration(
                    color: msg.role == 'user' ? const Color(0xFF28233F) : const Color(0xFF151525),
                    borderRadius: BorderRadius.circular(10),
                    border: Border.all(color: const Color(0x33B4AAFF)),
                  ),
                  child: Text(msg.content),
                ),
              );
            },
          ),

          // 2. Files Tab with Syntax Highlighting
          SingleChildScrollView(
            padding: const EdgeInsets.all(16),
            child: HighlightView(
              provider.activeFileContent,
              language: 'html',
              theme: atomOneDarkTheme,
              padding: const EdgeInsets.all(12),
              textStyle: const TextStyle(fontFamily: 'monospace', fontSize: 13),
            ),
          ),

          // 3. Preview Tab with WebView
          WebViewWidget(controller: _webViewController),
        ],
      ),
    );
  }
}
