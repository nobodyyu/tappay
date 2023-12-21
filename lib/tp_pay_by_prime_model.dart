class TPPayByPrimeModel {
  final String? prime;
  final String partnerKey;
  final String merchantId;
  final int amount;
  final Currency? currency;
  final String? orderNumber;
  final String details;
  final CardHolder cardHolder;
  final ResultUrl? resultUrl;
  TPPayByPrimeModel({
    this.prime,
    required this.partnerKey,
    required this.merchantId,
    required this.amount,
    this.currency = Currency.twd,
    this.orderNumber,
    required this.details,
    required this.cardHolder,
    this.resultUrl,
  });
  Map<String, dynamic> toMap() {
    Map<String, dynamic> data = {};
    if (prime != null) data['prime'] = prime;
    data['partnerKey'] = partnerKey;
    data['merchantId'] = merchantId;
    data['amount'] = amount;
    if (currency != null) data['currency'] = currency.toString();
    if (orderNumber != null) data['orderNumber'] = orderNumber;
    data['details'] = details;
    data['cardHolder'] = cardHolder.toMap();
    if (resultUrl != null) data['resultUrl'] = resultUrl!.toMap();
    return data;
  }
}

class CardHolder {
  final String phoneNumber;
  final String name;
  final String email;
  final String? zipCode;
  final String? address;
  final String? nationalId;
  final String? memberId;
  CardHolder({
    required this.phoneNumber,
    required this.name,
    required this.email,
    this.zipCode = '',
    this.address = '',
    this.nationalId = '',
    this.memberId = '',
  });

  Map<String, dynamic> toMap() {
    return {
      'phoneNumber': phoneNumber,
      'name': name,
      'email': email,
      'zipCode': zipCode,
      'address': address,
      'nationalId': nationalId,
      'memberId': memberId,
    };
  }
}

class ResultUrl {
  final String? frontendRedirectUrl;
  final String? backendNotifyUrl;
  final String? goBackUrl;
  ResultUrl({
    this.frontendRedirectUrl,
    this.backendNotifyUrl,
    this.goBackUrl,
  });
  Map<String, String> toMap() {
    Map<String, String> data = {};
    if (frontendRedirectUrl != null) {
      data['frontendRedirectUrl'] = frontendRedirectUrl!;
    }
    if (backendNotifyUrl != null) {
      data['backendNotifyUrl'] = backendNotifyUrl!;
    }
    if (goBackUrl != null) {
      data['goBackUrl'] = goBackUrl!;
    }
    return data;
  }
}

enum Currency {
  twd,
  usd;

  @override
  String toString() {
    switch (this) {
      case Currency.twd:
        return 'TWD';
      case Currency.usd:
        return 'USD';
    }
  }
}
