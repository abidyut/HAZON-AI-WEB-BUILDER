import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/api_service.dart';

class BuilderScreen extends StatefulWidget {
  const BuilderScreen({super.key});

  @override
  State<BuilderScreen> createState() => _BuilderScreenState();
}

class _BuilderScreenState extends State<BuilderScreen> {
  final TextEditingController _promptController = TextEditingController();
  String _selectedModel = 'auto';
  String _currentMode = 'build';

  @override
  Widget build(BuildContext context) {
    final provider = Provider.of<HazonProvider>(context);

    return Scaffold(
      appBar: AppBar(
        title: const Text('Hazon AI — Builder', style: TextStyle(fontWeight: FontWeight.bold)),
        backgroundColor: const Color(0xFF10101C),
        actions: [
          IconButton(
            icon: const Icon(Icons.settings),
            onPressed: () => Navigator.pushNamed(context, '/settings'),
          ),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Container(
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: const Color(0xFF10101C),
                borderRadius: BorderRadius.circular(16),
                border: Border.all(color: const Color(0x33B4AAFF)),
              ),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text('What do you want to build?', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
                  const SizedBox(height: 12),
                  TextField(
                    controller: _promptController,
                    maxLines: 4,
                    decoration: const InputDecoration(
                      hintText: 'Build a stunning portfolio website with dark theme and animations...',
                      border: InputBorder.none,
                    ),
                  ),
                  const Divider(color: Color(0x22B4AAFF)),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      DropdownButton<String>(
                        value: _selectedModel,
                        dropdownColor: const Color(0xFF151525),
                        items: const [
                          DropdownMenuItem(value: 'auto', child: Text('Auto-select')),
                          DropdownMenuItem(value: 'deepseek', child: Text('DeepSeek')),
                          DropdownMenuItem(value: 'glm', child: Text('GLM (Zhipu)')),
                          DropdownMenuItem(value: 'qwen', child: Text('Qwen')),
                        ],
                        onChanged: (val) => setState(() => _selectedModel = val ?? 'auto'),
                      ),
                      ElevatedButton.icon(
                        style: ElevatedButton.styleFrom(
                          backgroundColor: const Color(0xFF8B5CF6),
                          foregroundColor: Colors.white,
                          shape: RoundedCornerShape(8),
                        ),
                        onPressed: provider.isLoading ? null : () async {
                          if (_promptController.text.trim().isNotEmpty) {
                            await provider.createProject(
                              _promptController.text.trim(),
                              _selectedModel,
                              _currentMode,
                            );
                            if (mounted) Navigator.pushNamed(context, '/workspace');
                          }
                        },
                        icon: const Icon(Icons.arrow_forward, size: 16),
                        label: Text(provider.isLoading ? 'Building...' : 'Build Project'),
                      ),
                    ],
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}
