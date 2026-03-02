# Color-Sync Deterministic Visual Authentication (CDVA)

A scalable, offline-first visual authentication system that enables synchronized, cryptographically generated dynamic signals across millions of devices without centralized real-time validation.

This project demonstrates how large crowds can be validated in under one second using deterministic time-based cryptographic signals instead of per-user QR scanning.

## What This Project Does

Users book a time slot.

Each device derives a cryptographic slot key.

Every second, the app generates a deterministic HMAC-based signal.

The signal drives a full-screen animated visual pattern.

All users in the same slot display identical visuals at the same second.

A verification device independently computes the same signal.

If visuals match → entry allowed.

No live server validation required at the checkpoint.

## Core Concept

The visual signal is generated using:

slot_key = HKDF(daily_master_key, slot_id)

time_index = floor((device_time + offset) / 1000)

signal = HMAC_SHA256(slot_key, slot_id + ":" + time_index)

All animation parameters (color, rotation, grid density, motion speed) are derived directly from the HMAC output bytes.

This ensures:

Determinism across devices

Per-second dynamic change

No randomness

No screenshot replay viability

## Security Properties

HMAC-SHA256 prevents forgery

Daily key rotation limits exposure

Slot-based key isolation

Per-second signal mutation prevents replay

Global time synchronization mitigates clock drift (±60s tolerance)

Fully offline operation after initial sync

## Scalability

Designed for:

1,000,000+ concurrent users

20-minute peak windows

< 1 second verification

Zero server dependency at entry

Each device performs O(1) computation locally.

## Testing Focus

Cross-device deterministic validation

Time drift simulation

Replay attack resistance

Multi-device synchronization stability

Offline operation verification

## Tech Stack

HTML5 Canvas

Web Crypto API

HMAC-SHA256

HKDF-based key derivation

Monotonic time correction logic

## Why This Matters

Traditional QR validation does not scale for massive crowds and fails under network outages.

This approach replaces per-user verification with synchronized cryptographic consensus — enabling instant batch validation at scale.
