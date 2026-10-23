import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/api_service.dart';

class SettingsScreen extends StatefulWidget {
  const SettingsScreen({super.key});

  @override
  State<SettingsScreen> createState() => _SettingsScreenState();
}

class _SettingsScreenState extends State<SettingsScreen> {
  late TextEditingController _deepSeekController;
  late TextEditingController _glmController;
  late TextEditingController _qwenController;

  @override
  void initState() {
    super.initState();
    final p = Provider.of<HazonProvider>(context, listen: false);
    _deepSeekController = TextEditingController(text: p.deepSeekKey);
    _glmController = TextEditingController(text: p.glmKey);
    _qwenController = TextEditingController(text: p.qwenKey);
  }

  @override
  Widget build(BuildContext context) {
    final provider = Provider.of<HazonProvider>(context);

    return Scaffold(
      appBar: AppBar(
        title: const Text('API Settings'),
        backgroundColor: const Color(0xFF10101C),
      ),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          _buildField('DeepSeek API Key', 'For code generation & logic', _deepSeekController),
          const SizedBox(height: 16),
          _buildField('GLM (Zhipu AI) API Key', 'For discussion & planning', _glmController),
          const SizedBox(height: 16),
          _buildField('Qwen API Key', 'For image generation & mockups', _qwenController),
          const SizedBox(height: 24),
          ElevatedButton(
            style: ElevatedButton.styleFrom(
              backgroundColor: const Color(0xFF8B5CF6),
              padding: const EdgeInsets.symmetric(vertical: 14),
            ),
            onPressed: () {
              provider.saveKeys(
                _deepSeekController.text.trim(),
                _glmController.text.trim(),
                _qwenController.text.trim(),
              );
              ScaffoldMessenger.of(context).showSnackBar(
                const SnackBar(content: Text('Settings saved to local storage!')),
              );
            },
            child: const Text('Save API Keys', style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
          ),
        ],
      ),
    );
  }

  Widget _buildField(String title, String subtitle, TextEditingController controller) {
    return Container(
      padding: const EdgeInsets.all(14),
      decoration: BoxDecoration(
        color: const Color(0xFF151525),
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: const Color(0x33B4AAFF)),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(title, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
          Text(subtitle, style: const TextStyle(color: Colors.grey, fontSize: 11)),
          const SizedBox(height: 8),
          TextField(
            controller: controller,
            obscureText: true,
            decoration: const InputDecoration(
              hintText: 'Enter API Key...',
              border: OutlineInputBorder(),
            ),
          ),
        ],
      ),
    );
  }
}
