# Sync Contract

## Goal
Keep watch and phone aligned on the same mood-log schema.

## Canonical Record
- `id: Long`
- `createdAt: Long`
- `moodScore: Int` in `0..10`
- `phase: MoodPhase` with values `NORMAL`, `LOW_TROUGH`, `RELIEF`
- `triggers: List<Trigger>`
- `note: String?`

## Batch
- `schemaVersion: Int = 1`
- `records: List<MoodLogSyncRecord>`

## Sync Rules
- Watch remains offline-first.
- Watch writes locally first, then optionally pushes.
- `LOW_TROUGH` is the only phase allowed to persist.
- `moodScore` outside `0..10` must be rejected.
- `id` is local DB primary key.
- `createdAt` is server-agnostic ordering data.
- Phone should treat watch and phone records as the same schema.

## API Shape
- `MoodLogSyncGateway.push(batch)`
- `MoodLogSyncGateway.pullSince(cursor)`

## Implementation Notes
- No real sync is implemented yet.
- `NoOpMoodLogSyncer` stays as default on watch.
- Phone-side adapter should convert the same enum names and trigger names verbatim.
