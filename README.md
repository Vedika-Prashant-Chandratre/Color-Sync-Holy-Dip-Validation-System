Color-Sync Deterministic Visual Authentication (CDVA)

Offline-first, scalable, deterministic visual validation system designed for high-density event entry (e.g., holy dip slot verification).

This system allows 1 million+ users in the same time slot to generate an identical, time-synchronized dynamic visual signal — without real-time server validation.

🚀 Overview

Instead of scanning individual QR codes, this system works as follows:

All users in a specific slot generate the same dynamic visual pattern every second.

Police devices independently generate the same signal.

If the crowd’s screen matches the officer’s device → entry allowed.

Works fully offline after slot booking.

No centralized validation at the gate.
No network dependency.
< 1 second verification for entire batch.

🧠 Core Algorithm

Each device generates a deterministic signal every second:

slot_key = HKDF(daily_master_key, slot_id)

time_index = floor((device_time + offset) / 1000)

signal = HMAC_SHA256(slot_key, slot_id + ":" + time_index)

The signal hash bytes are used to generate:

Background color

Rotation angle

Grid density

Animation speed

Shape distortion

All visual parameters are derived directly from hash bytes.

Because HMAC is deterministic:

All users in same slot → same output

Police device → same output

No randomness involved

⏱ Clock Drift Handling

Devices may have ±60 seconds drift.

We handle this during slot booking:

offset = server_time - device_time

During execution:

corrected_time = Date.now() + offset

Then:

time_index = floor(corrected_time / 1000)
Tolerance Strategy

Police device accepts signals in a ±60 second window:

valid_time_index ∈ [t-60, t+60]

This prevents rejection due to minor time drift.

If drift > 2 minutes:

User must resync during booking phase

Entry denied

This prevents cascade desynchronization.

🔐 Replay & Screenshot Attack Prevention
1️⃣ Dynamic Per-Second Signal

Signal changes every second:

signal(t) ≠ signal(t+1)

So:

Screenshot becomes invalid in 1 second.

Recorded video becomes outdated instantly.

2️⃣ Cryptographic Binding

Signal depends on:

slot_id

daily_master_key

time_index

Without slot_key, attacker cannot compute future signals.

HMAC prevents forgery even if attacker sees output.

3️⃣ No Static Colors

Visual is not a simple color.
It includes:

Rotating geometry

Animated grid

Hash-derived distortion

Motion variation

So replayed images are detectable visually.

📈 Why It Scales to 1 Million Users
No Central Validation

At entry gate:

No database lookup

No QR scan

No network call

Each device computes locally in O(1) time.

Server Load Exists Only During Booking

Booking is horizontally scalable:

Stateless slot issuance

Sharded slot databases

CDN distribution of app

During entry:

Zero server dependency.

Police verification cost:

O(1) per batch

< 1 second

⚠ Biggest Failure Risk at Scale
Time Desynchronization Cascade

If large number of users have incorrect offset:

Entire batch could visually mismatch.

Causes crowd delay.

Mitigation

Strict offset calibration at booking.

Use monotonic clock after calibration.

Dual-window tolerance.

Police device can quickly cycle ±60 second window.

Other Risks
Risk	Mitigation
Key Leakage	Daily rotation of master key
Reverse Engineering	Secure enclave storage
Rooted Devices	App attestation
Slot Key Extraction	Derived per-slot keys only
🔑 Security Model Summary

HMAC-SHA256 ensures unforgeable signals.

HKDF isolates slot keys.

Daily master key rotation limits exposure window.

Offline deterministic generation eliminates network dependency.

Dynamic visual output prevents screenshot replay.

🏁 Final Properties

✔ 1M concurrent users
✔ Offline validation
✔ <1 second police verification
✔ No QR scanning
✔ No central server load at entry
✔ Screenshot resistant
✔ Replay resistant
