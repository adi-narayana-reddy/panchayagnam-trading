import 'package:flutter/material.dart';
import '../../../../constants/colors.dart';
import '../../../../constants/text_size_constants.dart';
import '../../../../models/demat_account_model.dart';

class BrokerSelectorWidget extends StatelessWidget {
  final List<DematAccountModel> accounts;
  final DematAccountModel? selectedAccount;
  final ValueChanged<DematAccountModel> onAccountChanged;

  const BrokerSelectorWidget({
    Key? key,
    required this.accounts,
    required this.selectedAccount,
    required this.onAccountChanged,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: AppColors.white,
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: AppColors.borderColor, width: 0.8),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              const Icon(Icons.account_balance_wallet_outlined,
                  color: AppColors.primary, size: 15),
              const SizedBox(width: 6),
              Text(
                'Select Broker Account',
                style: TextStyle(
                  fontSize: TextSizeConstants.small,
                  fontWeight: FontWeight.w600,
                  color: AppColors.textPrimary,
                ),
              ),
              const Spacer(),
              Container(
                padding:
                    const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                decoration: BoxDecoration(
                  color: AppColors.primaryBg,
                  borderRadius: BorderRadius.circular(20),
                ),
                child: Text(
                  '${accounts.length} accounts',
                  style: TextStyle(
                    fontSize: TextSizeConstants.xSmall,
                    color: AppColors.primary,
                    fontWeight: FontWeight.w500,
                  ),
                ),
              ),
            ],
          ),
          const SizedBox(height: 12),
          SingleChildScrollView(
            scrollDirection: Axis.horizontal,
            child: Row(
              children: accounts
                  .map((acc) => _BrokerChip(
                        account: acc,
                        isSelected: selectedAccount?.id == acc.id,
                        onTap: () => onAccountChanged(acc),
                      ))
                  .toList(),
            ),
          ),
        ],
      ),
    );
  }
}

class _BrokerChip extends StatelessWidget {
  final DematAccountModel account;
  final bool isSelected;
  final VoidCallback onTap;

  const _BrokerChip({
    required this.account,
    required this.isSelected,
    required this.onTap,
  });

  Color get _brokerColor {
    switch (account.brokerType.toUpperCase()) {
      case 'ANGEL':
        return const Color(0xFFE53935);
      case 'ZERODHA':
        return const Color(0xFF387ED1);
      case 'UPSTOX':
        return const Color(0xFF6200EA);
      case 'FYERS':
        return const Color(0xFF00897B);
      default:
        return AppColors.primary;
    }
  }

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: onTap,
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 200),
        margin: const EdgeInsets.only(right: 8),
        padding:
            const EdgeInsets.symmetric(horizontal: 12, vertical: 10),
        decoration: BoxDecoration(
          color: isSelected ? AppColors.primaryBg : AppColors.scaffold,
          borderRadius: BorderRadius.circular(10),
          border: Border.all(
            color: isSelected
                ? AppColors.primary
                : AppColors.borderColor,
            width: isSelected ? 1.5 : 0.8,
          ),
        ),
        child: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            Container(
              width: 28,
              height: 28,
              decoration: BoxDecoration(
                color: _brokerColor.withOpacity(0.12),
                borderRadius: BorderRadius.circular(6),
              ),
              child: Center(
                child: Text(
                  account.brokerType.isNotEmpty
                      ? account.brokerType[0].toUpperCase()
                      : 'B',
                  style: TextStyle(
                    fontSize: TextSizeConstants.small,
                    fontWeight: FontWeight.w700,
                    color: _brokerColor,
                  ),
                ),
              ),
            ),
            const SizedBox(width: 8),
            Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  account.clientId,
                  style: TextStyle(
                    fontSize: TextSizeConstants.small,
                    fontWeight: FontWeight.w600,
                    color: isSelected
                        ? AppColors.primary
                        : AppColors.textPrimary,
                  ),
                ),
                Text(
                  account.brokerType,
                  style: TextStyle(
                    fontSize: TextSizeConstants.xSmall,
                    color: isSelected
                        ? AppColors.primaryLight
                        : AppColors.textSecondary,
                  ),
                ),
              ],
            ),
            if (isSelected) ...[
              const SizedBox(width: 8),
              const Icon(Icons.check_circle_rounded,
                  color: AppColors.primary, size: 14),
            ],
          ],
        ),
      ),
    );
  }
}
