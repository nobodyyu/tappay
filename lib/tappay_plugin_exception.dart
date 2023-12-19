class TappayPluginException implements Exception {
  final String message;
  TappayPluginException({required this.message});
  @override
  String toString() => 'TappayPluginException($message)';
}
