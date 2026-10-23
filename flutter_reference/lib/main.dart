import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:hive_flutter/hive_flutter.dart';
import 'screens/builder_screen.dart';
import 'screens/workspace_screen.dart';
import 'screens/settings_screen.dart';
import 'services/api_service.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Hive.initFlutter();
  await Hive.openBox('hazon_projects');
  await Hive.openBox('hazon_settings');

  runApp(
    MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => HazonProvider()),
      ],
      child: const HazonApp(),
    ),
  );
}

class HazonApp extends StatelessWidget {
  const HazonApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Hazon AI',
      debugShowCheckedModeBanner: false,
      themeMode: ThemeMode.dark,
      darkTheme: ThemeData(
        brightness: Brightness.dark,
        scaffoldBackgroundColor: const Color(0xFF090911),
        canvasColor: const Color(0xFF10101C),
        cardColor: const Color(0xFF151525),
        colorScheme: const ColorScheme.dark(
          primary: Color(0xFF8B5CF6),
          secondary: Color(0xFFA78BFA),
          surface: Color(0xFF10101C),
          surfaceContainer: Color(0xFF151525),
        ),
        fontFamily: 'Inter',
      ),
      initialRoute: '/',
      routes: {
        '/': (context) => const BuilderScreen(),
        '/workspace': (context) => const WorkspaceScreen(),
        '/settings': (context) => const SettingsScreen(),
      },
    );
  }
}
