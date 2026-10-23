import 'dart:convert';
import 'package:flutter/foundation.dart';
import 'package:dio/dio.dart';
import 'package:hive_flutter/hive_flutter.dart';
import '../models/project_model.dart';

class ApiService {
  final Dio _dio = Dio(
    BaseOptions(
      connectTimeout: const Duration(seconds: 30),
      receiveTimeout: const Duration(seconds: 60),
    ),
  );

  Future<String> callDeepSeek({
    required String apiKey,
    required String prompt,
    String model = 'deepseek-chat',
  }) async {
    try {
      final response = await _dio.post(
        'https://api.deepseek.com/chat/completions',
        options: Options(headers: {
          'Authorization': 'Bearer $apiKey',
          'Content-Type': 'application/json',
        }),
        data: {
          'model': model,
          'messages': [
            {'role': 'system', 'content': 'You are Hazon AI code generator.'},
            {'role': 'user', 'content': prompt},
          ],
        },
      );
      return response.data['choices'][0]['message']['content'] ?? '';
    } catch (e) {
      return 'Error calling DeepSeek API: $e';
    }
  }

  Future<String> callGlmChat({
    required String apiKey,
    required List<ChatMessage> history,
    required String newMessage,
  }) async {
    try {
      final messages = history.map((m) => {'role': m.role, 'content': m.content}).toList();
      messages.add({'role': 'user', 'content': newMessage});

      final response = await _dio.post(
        'https://open.bigmodel.cn/api/paas/v4/chat/completions',
        options: Options(headers: {
          'Authorization': 'Bearer $apiKey',
          'Content-Type': 'application/json',
        }),
        data: {
          'model': 'glm-4',
          'messages': messages,
        },
      );
      return response.data['choices'][0]['message']['content'] ?? '';
    } catch (e) {
      return 'Error calling GLM API: $e';
    }
  }
}

class HazonProvider extends ChangeNotifier {
  final ApiService _api = ApiService();
  final Box _projectsBox = Hive.box('hazon_projects');
  final Box _settingsBox = Hive.box('hazon_settings');

  ProjectModel? currentProject;
  List<ProjectModel> projects = [];
  List<ChatMessage> currentMessages = [];
  String activeFileName = 'index.html';
  String activeFileContent = '';
  bool isLoading = false;

  HazonProvider() {
    loadProjects();
  }

  String get deepSeekKey => _settingsBox.get('deepseek_key', defaultValue: '');
  String get glmKey => _settingsBox.get('glm_key', defaultValue: '');
  String get qwenKey => _settingsBox.get('qwen_key', defaultValue: '');

  void saveKeys(String deepSeek, String glm, String qwen) {
    _settingsBox.put('deepseek_key', deepSeek);
    _settingsBox.put('glm_key', glm);
    _settingsBox.put('qwen_key', qwen);
    notifyListeners();
  }

  void loadProjects() {
    projects = _projectsBox.values
        .map((e) => ProjectModel.fromMap(Map<dynamic, dynamic>.from(e)))
        .toList();
    notifyListeners();
  }

  Future<void> createProject(String prompt, String model, String mode) async {
    isLoading = true;
    notifyListeners();

    // Use Isolate / compute for heavy scaffolding
    final id = DateTime.now().millisecondsSinceEpoch.toString();
    final project = await compute(_scaffoldProjectData, {
      'id': id,
      'prompt': prompt,
      'model': model,
      'mode': mode,
    });

    currentProject = project;
    projects.insert(0, project);
    await _projectsBox.put(id, project.toMap());

    activeFileName = 'index.html';
    activeFileContent = project.files['index.html'] ?? '';

    currentMessages = [
      ChatMessage(
        id: '1',
        role: 'assistant',
        content: 'Hello! I have created the project files for "$prompt". Explore code in the Files tab or preview in Preview tab.',
        timestamp: DateTime.now(),
      )
    ];

    isLoading = false;
    notifyListeners();
  }

  static ProjectModel _scaffoldProjectData(Map<String, dynamic> params) {
    final title = (params['prompt'] as String).length > 30
        ? '${(params['prompt'] as String).substring(0, 28)}...'
        : params['prompt'] as String;

    return ProjectModel(
      id: params['id'],
      title: title,
      prompt: params['prompt'],
      model: params['model'],
      mode: params['mode'],
      createdAt: DateTime.now(),
      files: {
        'index.html': '<!DOCTYPE html>\n<html>\n<head>\n  <link rel="stylesheet" href="styles.css">\n</head>\n<body>\n  <h1>${params['prompt']}</h1>\n  <script src="app.js"></script>\n</body>\n</html>',
        'styles.css': 'body { background: #090911; color: #f5f3ff; font-family: sans-serif; padding: 20px; }',
        'app.js': 'console.log("Hazon AI Studio Running");',
      },
    );
  }
}
