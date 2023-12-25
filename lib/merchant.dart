enum Merchant {
  directPay,
  linePay,
  jkoPay,
  easyWallet,
  googlePay;

  @override
  String toString() {
    switch (this) {
      case Merchant.directPay:
        return '信用卡/簽帳卡';
      case Merchant.linePay:
        return 'Line Pay';
      case Merchant.jkoPay:
        return '街口支付';
      case Merchant.easyWallet:
        return '悠遊卡錢包';
      case Merchant.googlePay:
        return 'Google Pay';
    }
  }
}
