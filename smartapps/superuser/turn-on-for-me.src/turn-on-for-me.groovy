/**
 *  Turn On For Me
 *
 *  Author: David Lane
 */

// Automatically generated. Make future change here.
definition(
    name: "Turn On For Me",
    namespace: "",
    author: "David Lane",
    description: "Turns lights on when a presence is newly detected, when a door/window is opened, or when a door/window/whatever is moved.",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience%402x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience%402x.png")

preferences {
	section("When any arrive...") {
		input "presence", "capability.presenceSensor",
            title: "Who?",
            multiple: true
	}
    
    section("Or when...") {
    	input "contact", "capability.contactSensor",
        	title: "Something is opened?",
            multiple: true,
            required: false

		input "moved", "capability.accelerationSensor",
        	title: "Something is moved?",
            multiple: true,
            required: false
	}
    
	section("Turn on a switch...") {
		input "switches", "capability.switch",
        	title: "Which?",
            multiple: true
    }
}

/**
 * Install handler.
 */
def installed()
{
    init()
}

/**
 * Update handler.
 */
def updated()
{
	unsubscribe()
    init()
}

/**
 * Initialize.
 */
def init()
{
	log.debug "Initializing: ${preferences}"
    
	subscribe presence, "presence", handler
	subscribe contact, "contact", handler
	subscribe moved, "acceleration", handler
}

/**
 * Handle the event
 */
def handler(evt)
{
	log.debug "Presence: ${evt.value}"
    
	if (evt.value == "present" || evt.value == "active" || evt.value == "open") {
		switches.on()
	}
}
