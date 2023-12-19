enum ServerType {
  sandbox,
  production;

  @override
  String toString() {
    if (this == ServerType.sandbox) {
      return 'sandbox';
    } else {
      return 'production';
    }
  }
}
