load("//learning/gemini/agents/skill_rules:skills.bzl", "agent_skill")
load("//learning/gemini/agents/skills/skill_creator/scripts:skill_defs.bzl", "validate_skill_test")

package(default_visibility = ["//visibility:public"])

agent_skill(
    name = "android-maps-ktx",
    srcs = [
        "SKILL.md",
        "references/clustering-flows.md",
        "references/collection-flows.md",
        "references/gotchas.md",
        "references/location-flows.md",
    ],
)

validate_skill_test(name = "validate_android_maps_ktx_skill_test")
