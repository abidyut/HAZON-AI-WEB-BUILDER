class ProjectModel {
  final String id;
  final String title;
  final String prompt;
  final String model;
  final String mode; // 'build' or 'plan'
  final DateTime createdAt;
  final Map<String, String> files; // 'index.html', 'styles.css', 'app.js'

  ProjectModel({
    required this.id,
    required this.title,
    required this.prompt,
    required this.model,
    required this.mode,
    required this.createdAt,
    required this.files,
  });

  Map<String, dynamic> toMap() => {
    'id': id,
    'title': title,
    'prompt': prompt,
    'model': model,
    'mode': mode,
    'createdAt': createdAt.toIso8601String(),
    'files': files,
  };

  factory ProjectModel.fromMap(Map<dynamic, dynamic> map) => ProjectModel(
    id: map['id'] ?? '',
    title: map['title'] ?? '',
    prompt: map['prompt'] ?? '',
    model: map['model'] ?? 'auto',
    mode: map['mode'] ?? 'build',
    createdAt: DateTime.tryParse(map['createdAt'] ?? '') ?? DateTime.now(),
    files: Map<String, String>.from(map['files'] ?? {}),
  );
}

class ChatMessage {
  final String id;
  final String role; // 'user' or 'assistant'
  final String content;
  final DateTime timestamp;

  ChatMessage({
    required this.id,
    required this.role,
    required this.content,
    required this.timestamp,
  });

  Map<String, dynamic> toMap() => {
    'id': id,
    'role': role,
    'content': content,
    'timestamp': timestamp.toIso8601String(),
  };

  factory ChatMessage.fromMap(Map<dynamic, dynamic> map) => ChatMessage(
    id: map['id'] ?? '',
    role: map['role'] ?? 'user',
    content: map['content'] ?? '',
    timestamp: DateTime.tryParse(map['timestamp'] ?? '') ?? DateTime.now(),
  );
}
