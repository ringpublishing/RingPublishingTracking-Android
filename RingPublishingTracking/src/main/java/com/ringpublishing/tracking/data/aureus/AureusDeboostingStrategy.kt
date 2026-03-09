package com.ringpublishing.tracking.data.aureus

/**
 * Aureus deboosting strategy
 * @param text: deboosting strategy name as expected by Aureus
 */
enum class AureusDeboostingStrategy(val text: String) {
    /**
     * User was engaged with content
     */
    CLICK("click"),

    /**
     * User saw content multiple times but never interacted with it
     */
    VIEW("view");
}
