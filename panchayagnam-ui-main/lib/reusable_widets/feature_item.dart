import 'package:flutter/material.dart';
import '../constants/colors.dart';

class FeatureItem extends StatelessWidget {
  final String title;
  final String description;

  const FeatureItem({
    Key? key,
    required this.title,
    required this.description,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    final screenHeight = MediaQuery.of(context).size.height;
    
    return Padding(
      padding: EdgeInsets.symmetric(vertical: screenHeight * 0.01),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Container(
            width: screenHeight * 0.012,
            height: screenHeight * 0.012,
            margin: EdgeInsets.only(top: screenHeight * 0.006),
            decoration: const BoxDecoration(
              color: AppColors.featureIconColor,
              shape: BoxShape.circle,
            ),
          ),
          SizedBox(width: screenHeight * 0.015),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  title,
                  style: TextStyle(
                    fontSize: screenHeight * 0.018,
                    fontWeight: FontWeight.w600,
                    color: AppColors.textPrimary,
                  ),
                ),
                SizedBox(height: screenHeight * 0.003),
                Text(
                  description,
                  style: TextStyle(
                    fontSize: screenHeight * 0.015,
                    color: AppColors.textSecondary,
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
