package com.mssimulator.data

/**
 * Remote game data hosted at github.com/r00tIsRoot/MSSimulatorData.
 * Fetched at app start; falls back to the embedded [SAMPLE_SKILLS_JSON] data on failure.
 */
private const val REMOTE_BASE =
    "https://raw.githubusercontent.com/r00tIsRoot/MSSimulatorData/main"

const val REMOTE_SKILLS_URL = "$REMOTE_BASE/skills.json"
const val REMOTE_BOSSES_URL = "$REMOTE_BASE/bosses.json"
