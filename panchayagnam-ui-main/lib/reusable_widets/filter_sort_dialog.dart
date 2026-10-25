import 'package:easy_trade_web/reusable_widets/sort_option.dart';
import 'package:easy_trade_web/reusable_widets/status_chip_widget.dart';
import 'package:flutter/material.dart';

import '../constants/colors.dart';

class FilterSortDialog extends StatefulWidget {
  final String currentStatus;
  final String currentSortBy;
  final Function(String status, String sortBy) onApply;

  const FilterSortDialog({
    super.key,
    required this.currentStatus,
    required this.currentSortBy,
    required this.onApply,
  });

  @override
  State<FilterSortDialog> createState() => _FilterSortDialogState();
}

class _FilterSortDialogState extends State<FilterSortDialog> {
  late String _selectedStatus;
  late String _selectedSortBy;

  @override
  void initState() {
    super.initState();
    _selectedStatus = widget.currentStatus;
    _selectedSortBy = widget.currentSortBy;
  }

  @override
  Widget build(BuildContext context) {
    return Dialog(
      backgroundColor: Colors.white,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      child: Container(
        width: 360,
        constraints: const BoxConstraints(maxHeight: 580),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            // Header
            Container(
              padding: const EdgeInsets.all(20),
              decoration: const BoxDecoration(
                border: Border(
                  bottom: BorderSide(color: Color(0xFFE5E7EB), width: 1),
                ),
              ),
              child: Row(
                children: [
                  const Text(
                    'Filter & Sort',
                    style: TextStyle(
                      fontSize: 18,
                      fontWeight: FontWeight.bold,
                      color: Color(0xFF111827),
                    ),
                  ),
                  const Spacer(),
                  IconButton(
                    onPressed: () => Navigator.of(context).pop(),
                    icon: const Icon(Icons.close, size: 20),
                    padding: EdgeInsets.zero,
                    constraints: const BoxConstraints(),
                    visualDensity: VisualDensity.compact,
                  ),
                ],
              ),
            ),

            // Scrollable Content
            Flexible(
              child: SingleChildScrollView(
                padding: const EdgeInsets.all(20),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    // Status Section
                    const Text(
                      'Status',
                      style: TextStyle(
                        fontSize: 13,
                        fontWeight: FontWeight.w600,
                        color: Color(0xFF111827),
                      ),
                    ),
                    const SizedBox(height: 10),

                    Wrap(
                      spacing: 8,
                      runSpacing: 8,
                      children: [
                        StatusChip(
                          label: 'All',
                          isSelected: _selectedStatus == 'ALL',
                          onTap: () {
                            setState(() {
                              _selectedStatus = 'ALL';
                            });
                          },
                        ),
                        StatusChip(
                          label: 'ACTIVE',
                          isSelected: _selectedStatus == 'ACTIVE',
                          onTap: () {
                            setState(() {
                              _selectedStatus = 'ACTIVE';
                            });
                          },
                        ),
                        StatusChip(
                          label: 'INACTIVE',
                          isSelected: _selectedStatus == 'INACTIVE',
                          onTap: () {
                            setState(() {
                              _selectedStatus = 'INACTIVE';
                            });
                          },
                        ),
                      ],
                    ),

                    const SizedBox(height: 24),

                    const Text(
                      'Sort By',
                      style: TextStyle(
                        fontSize: 13,
                        fontWeight: FontWeight.w600,
                        color: Color(0xFF111827),
                      ),
                    ),
                    const SizedBox(height: 10),

                    SortOption(
                      label: 'Name (A-Z)',
                      isSelected: _selectedSortBy == 'Name (A-Z)',
                      onTap: () {
                        setState(() {
                          _selectedSortBy = 'Name (A-Z)';
                        });
                      },
                    ),
                    SortOption(
                      label: 'Name (Z-A)',
                      isSelected: _selectedSortBy == 'Name (Z-A)',
                      onTap: () {
                        setState(() {
                          _selectedSortBy = 'Name (Z-A)';
                        });
                      },
                    ),
                    SortOption(
                      label: 'Portfolio (High to Low)',
                      isSelected: _selectedSortBy == 'Portfolio (High to Low)',
                      onTap: () {
                        setState(() {
                          _selectedSortBy = 'Portfolio (High to Low)';
                        });
                      },
                    ),
                    SortOption(
                      label: 'Portfolio (Low to High)',
                      isSelected: _selectedSortBy == 'Portfolio (Low to High)',
                      onTap: () {
                        setState(() {
                          _selectedSortBy = 'Portfolio (Low to High)';
                        });
                      },
                    ),
                    SortOption(
                      label: 'Date Created (Newest)',
                      isSelected: _selectedSortBy == 'Date Created (Newest)',
                      onTap: () {
                        setState(() {
                          _selectedSortBy = 'Date Created (Newest)';
                        });
                      },
                    ),
                    SortOption(
                      label: 'Date Created (Oldest)',
                      isSelected: _selectedSortBy == 'Date Created (Oldest)',
                      onTap: () {
                        setState(() {
                          _selectedSortBy = 'Date Created (Oldest)';
                        });
                      },
                    ),
                  ],
                ),
              ),
            ),

            // Action Buttons
            Container(
              padding: const EdgeInsets.all(20),
              decoration: const BoxDecoration(
                border: Border(
                  top: BorderSide(color: Color(0xFFE5E7EB), width: 1),
                ),
              ),
              child: Row(
                children: [
                  Expanded(
                    child: TextButton(
                      onPressed: () {
                        Navigator.of(context).pop();
                      },
                      style: TextButton.styleFrom(
                        padding: const EdgeInsets.symmetric(vertical: 12),
                      ),
                      child: const Text(
                        'Cancel',
                        style: TextStyle(
                          fontSize: 13,
                          fontWeight: FontWeight.w600,
                          color: Color(0xFF6B7280),
                        ),
                      ),
                    ),
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: ElevatedButton(
                      onPressed: () {
                        widget.onApply(_selectedStatus, _selectedSortBy);
                        Navigator.of(context).pop();
                      },
                      style: ElevatedButton.styleFrom(
                        backgroundColor: AppColors.primaryBlue,
                        padding: const EdgeInsets.symmetric(vertical: 12),
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(8),
                        ),
                        elevation: 0,
                      ),
                      child: const Text(
                        'Apply',
                        style: TextStyle(
                          fontSize: 13,
                          fontWeight: FontWeight.w600,
                          color: Colors.white,
                        ),
                      ),
                    ),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

}

