part of 'ip_bloc.dart';

abstract class IpEvent {}

// ── Save IPs ─────────────────────────────────────────────────────────────────
class SaveIpsEvent extends IpEvent {
  final List<String> ips;
  SaveIpsEvent(this.ips);
}

// ── Fetch All IPs ─────────────────────────────────────────────────────────────
class FetchAllIpsEvent extends IpEvent {}

// ── Fetch IP Report ───────────────────────────────────────────────────────────
class FetchIpReportEvent extends IpEvent {}

// ── Check IP Health ───────────────────────────────────────────────────────────
class CheckIpHealthEvent extends IpEvent {
  final List<String> ips;
  CheckIpHealthEvent(this.ips);
}

// ── Clear Save State ──────────────────────────────────────────────────────────
class ClearSaveStateEvent extends IpEvent {}

// ── Clear Health State ────────────────────────────────────────────────────────
class ClearHealthStateEvent extends IpEvent {}