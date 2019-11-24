const ATTRIBUTE = "ATTRIBUTE"
const REFERENCE = "REFERENCE"

/***
 *
 */
class EObject {
    constructor() {
        this.id = null
        this.package = null
        this.className = null
        this.features = new Map()
        this.resource = null
    }
}

/**
 *
 */
class EFeature {
    constructor(owner) {
        this.name = null
        this.type = ATTRIBUTE
        this.owner = owner
        this.values = new Map()
        this.isContainment = true
    }

    ATTRIBUTE() {
        return ATTRIBUTE
    }

    REFERENCE() {
        return REFERENCE
    }
}

/**
 *
 */
class Resource {
    constructor() {
        this.idToEObjectMap = new Map()
        this.eObjectToIdMap = new Map()
        this.contents = new EFeature()
        this.contents.name = 'resource'
        this.changeEvents = []
        this.packages = []
    }

    setId(eObject, id) {
        this.eObjectToIdMap.set(eObject, id)
        this.idToEObjectMap.set(id, eObject)
    }

    getId(eObject) {
        return this.eObjectToIdMap.get(eObject)
    }

    setEObject(id, eObject) {
        this.eObjectToIdMap.set(eObject, id)
        this.idToEObjectMap.set(id, eObject)
    }

    getEObject(id) {
        return this.idToEObjectMap.get(id)
    }
}

/**
 *
 */
class ChangeEvent {
    constructor() {
        this.value = null
        this.drawers = new Map()
    }

    replay() {
    }

    draw(graphs) {
        for (let [key, graph] of graphs) {
            let drawer = this.drawers.get(key)
            if (drawer != null) {
                drawer(graph, this)
            }
        }
    }

    drawReplay(graphs) {
        this.replay()
        this.draw(graphs)
    }
}

/**
 *
 */
class SessionEvent extends ChangeEvent {
    constructor() {
        super();
        this.id = null
        this.time = null
        this.session = null
    }
}

/**
 *
 */
class CreateElementEvent extends ChangeEvent {
    constructor() {
        super();
        this.id = null
        this.package = null
        this.className = null
        this.resource = null
        this.eObject = null
    }

    replay() {
        this.eObject = new EObject()
        let eObject = this.eObject
        eObject.id = this.id
        eObject.package = this.package
        eObject.className = this.className
        eObject.resource = this.resource
        this.resource.setId(eObject, eObject.id)
    }
}

/**
 *
 */
class DeleteElementEvent extends ChangeEvent {
    constructor() {
        super();
        this.id = null
        this.package = null
        this.className = null
        this.resource = null
        this.eObject = null
    }

    replay() {
        this.eObject = new EObject()
        let eObject = this.eObject
        eObject.id = this.id
        eObject.package = this.package
        eObject.className = this.className
        eObject.resource = this.resource
    }
}

/**
 *
 */
class AddToResourceEvent extends ChangeEvent {
    constructor() {
        super();
        this.index = null
        this.resource = null
        this.valueId = null
        this.value = null
    }

    replay() {
        this.value = this.resource.getEObject(this.valueId)
        this.resource.contents.values.set(this.index, this.value)
    }
}

/**
 *
 */
class RemoveFromResourceEvent extends ChangeEvent {
    constructor() {
        super();
        this.index = null
        this.resource = null
        this.valueId = null
        this.value = null
    }

    replay() {
        this.value = this.resource.getEObject(this.valueId)
        this.resource.contents.values.set(this.index, this.value)
        let key = null
        for (let entry of this.resource.contents.values) {
            key = entry.key
            let value = entry.value
            if (this.value == value) {
                break;
            }
            console
        }
        this.resource.contents.values.delete(key)
    }
}

/**
 *
 */
class SetAttributeEvent extends ChangeEvent {
    constructor() {
        super();
        this.resource = null
        this.featureName = null
        this.targetId = null
        this.valueId = null
        this.value = null
        this.target = null
        this.oldValueId = null
        this.oldValue = null
    }

    replay() {
        this.eObject = this.resource.getEObject(this.targetId)
        let feature = this.eObject.features.get(this.featureName)
        if (feature == null) {
            feature = new EFeature(this.eObject)
            feature.name = this.featureName
            this.eObject.features.set(this.featureName, feature)
        }
        feature.values.set(0, this.value)
    }
}

/**
 *
 */
class UnsetAttributeEvent extends ChangeEvent {
    constructor() {
        super();
        this.resource = null
        this.featureName = null
        this.targetId = null
        this.valueId = null
        this.value = null
        this.target = null
        this.oldValueId = null
        this.oldValue = null
    }

    replay() {
        this.eObject = this.resource.getEObject(this.targetId)
        let feature = this.eObject.features.get(this.featureName)
        if (feature == null) {
            feature = new EFeature(this.eObject)
            feature.name = this.featureName
            this.eObject.features.set(this.featureName, feature)
        }
        feature.values.set(0, null)
    }
}

/**
 *
 */
class SetReferenceEvent extends ChangeEvent {
    constructor() {
        super();
        this.resource = null
        this.featureName = null
        this.targetId = null
        this.valueId = null
        this.value = null
        this.target = null
        this.oldValueId = null
        this.oldValue = null
    }

    replay() {
        this.eObject = this.resource.getEObject(this.targetId)
        if (this.eObject == null) {
            this.eObject = new EObject()
            let eObject = this.eObject
            eObject.id = this.targetId
            eObject.className = this.className
            eObject.resource = this.resource
            this.resource.setId(eObject, eObject.id)
        }
        let feature = this.eObject.features.get(this.featureName)
        if (feature == null) {
            feature = new EFeature(this.eObject)
            feature.name = this.featureName
            feature.type = REFERENCE
            this.eObject.features.set(this.featureName, feature)
            if (metaEcore.contaimentReferences.includes(this.featureName)) {
                feature.isContainment = true
            } else {
                feature.isContainment = false
            }
        }
        this.value = this.resource.getEObject(this.valueId)
        feature.values.set(0, this.value)
    }
}

class UnsetReferenceEvent extends ChangeEvent {
    constructor() {
        super();
        this.resource = null
        this.featureName = null
        this.targetId = null
        this.valueId = null
        this.value = null
        this.target = null
        this.oldValueId = null
        this.oldValue = null
    }

    replay() {
        this.eObject = this.resource.getEObject(this.targetId)
        if (this.eObject == null) {
            this.eObject = new EObject()
            let eObject = this.eObject
            eObject.id = this.targetId
            eObject.className = this.className
            eObject.resource = this.resource
            this.resource.setId(eObject, eObject.id)
        }
        let feature = this.eObject.features.get(this.featureName)
        if (feature == null) {
            feature = new EFeature(this.eObject)
            feature.name = this.featureName
            feature.type = REFERENCE
            this.eObject.features.set(this.featureName, feature)
            if (metaEcore.contaimentReferences.includes(this.featureName)) {
                feature.isContainment = true
            } else {
                feature.isContainment = false
            }
        }
        this.value = this.resource.getEObject(this.valueId)
        feature.values.set(0, null)
    }
}


class AddToReferenceEvent extends ChangeEvent {
    constructor() {
        super();
        this.resource = null
        this.featureName = null
        this.targetId = null
        this.valueId = null
        this.value = null
        this.target = null
        this.index = null
        this.oldValueId = null
        this.oldValue = null
    }

    replay() {
        this.eObject = this.resource.getEObject(this.targetId)

        if (this.eObject == null) {
            this.eObject = new EObject()
            let eObject = this.eObject
            eObject.id = this.targetId
            eObject.className = this.className
            eObject.resource = this.resource
            this.resource.setId(eObject, eObject.id)
        }

        let feature = this.eObject.features.get(this.featureName)
        if (feature == null) {
            feature = new EFeature(this.eObject)
            feature.name = this.featureName
            feature.type = REFERENCE
            this.eObject.features.set(this.featureName, feature)
            if (metaEcore.contaimentReferences.includes(this.featureName)) {
                feature.isContainment = true
            } else {
                feature.isContainment = false
            }
        }
        this.value = this.resource.getEObject(this.valueId)

        // move to tempValues for keys and values larger than or equal to index
        let tempValues = new Map()
        for (let [key, value] of feature.values) {
            if (key >= this.index) {
                tempValues.set(key + 1, value)
            }
        }

        //move keys and values in tempValues to the real feature
        feature.values.set(this.index, this.value)
        for (let [key, value] of tempValues) {
            feature.values.set(key, value)
        }
        tempValues.clear()
    }
}

class RemoveFromReference extends ChangeEvent {
    constructor() {
        super();
    }
}

class AddToAttributeEvent extends ChangeEvent {
    constructor() {
        super();
        this.resource = null
        this.featureName = null
        this.targetId = null
        this.valueId = null
        this.value = null
        this.target = null
        this.index = null
        this.oldValueId = null
        this.oldValue = null
    }

    replay() {
        this.eObject = this.resource.getEObject(this.targetId)
        let feature = this.eObject.features.get(this.featureName)
        if (feature == null) {
            feature = new EFeature(this.eObject)
            feature.name = this.featureName
            feature.type = ATTRIBUTE
            this.eObject.features.set(this.featureName, feature)
            if (metaEcore.contaimentReferences.includes(this.featureName)) {
                feature.isContainment = true
            } else {
                feature.isContainment = false
            }
        }

        // move to tempValues for keys and values larger than or equal to index
        let tempValues = new Map()
        for (let [key, value] of feature.values) {
            if (key >= this.index) {
                tempValues.set(key + 1, value)
            }
        }

        //move keys and values in tempValues to the real feature
        feature.values.set(this.index, this.value)
        for (let [key, value] of tempValues) {
            feature.values.set(key, value)
        }
        tempValues.clear()
    }
}


/**
 * CbpUtil
 */
class CbpUtil {
    constructor() {
    }

    // Changes XML to JSON
    xmlToJson(xml) {

        // Create the return object
        var obj = {};

        if (xml.nodeType == 1) { // element
            // do attributes
            if (xml.attributes.length > 0) {
                obj["@attributes"] = {};
                for (var j = 0; j < xml.attributes.length; j++) {
                    var attribute = xml.attributes.item(j);
                    obj["@attributes"][attribute.nodeName] = attribute.nodeValue;
                }
            }
        } else if (xml.nodeType == 3) { // text
            obj = xml.nodeValue;
        }

        // do children
        // If just one text node inside
        if (xml.hasChildNodes() && xml.childNodes.length === 1 && xml.childNodes[0].nodeType === 3) {
            obj = xml.childNodes[0].nodeValue;
        } else if (xml.hasChildNodes()) {
            for (var i = 0; i < xml.childNodes.length; i++) {
                var item = xml.childNodes.item(i);
                var nodeName = item.nodeName;
                if (typeof (obj[nodeName]) == "undefined") {
                    obj[nodeName] = this.xmlToJson(item);
                } else {
                    if (typeof (obj[nodeName].push) == "undefined") {
                        var old = obj[nodeName];
                        obj[nodeName] = [];
                        obj[nodeName].push(old);
                    }
                    obj[nodeName].push(this.xmlToJson(item));
                }
            }
        }
        return obj;
    }

    /**
     * method to read a text file
     * @param {*} file
     */
    readTextFile(file) {
        var request = new XMLHttpRequest()
        request.open('GET', file, false)
        request.send(null)
        if (request.status === 200) {
            return request.responseText
        }
        return ""
    }
}


/**
 * CbpPlayer
 */
class CbpPlayer {

    constructor() {
        this.changeEventLines = null
        this.resource = new Resource()
    }

    /**
     * method to load a Cbp file
     * @param {*} file
     */
    loadCbp(file) {
        let cbpUtil = new CbpUtil()
        let cbpText = CbpUtil.prototype.readTextFile(file)
        this.changeEventLines = cbpText.split('\n')
        this.changeEventLines = this.changeEventLines.map(line => line.trim())
        this.changeEventLines.pop() // remove the last empty line

        let oParser = new DOMParser()
        for (let xmlString of this.changeEventLines) {
            let domEvent = oParser.parseFromString(xmlString, "application/xml").firstChild
            let eventType = domEvent.nodeName;
            console.log(eventType)
            if (eventType == 'session') {
                let changeEvent = new SessionEvent()
                changeEvent.id = domEvent.getAttribute('id')
                changeEvent.time = domEvent.getAttribute('time')
                this.resource.changeEvents.push(changeEvent)
                console;
            } else if (eventType == 'create') {
                let changeEvent = new CreateElementEvent()
                changeEvent.id = domEvent.getAttribute('id')
                changeEvent.package = domEvent.getAttribute('epackage')
                changeEvent.className = domEvent.getAttribute('eclass')
                changeEvent.resource = this.resource
                this.resource.changeEvents.push(changeEvent)
                console;
            } else if (eventType == 'delete') {
                let changeEvent = new DeleteElementEvent()
                changeEvent.id = domEvent.getAttribute('id')
                changeEvent.package = domEvent.getAttribute('epackage')
                changeEvent.className = domEvent.getAttribute('eclass')
                changeEvent.resource = this.resource
                this.resource.changeEvents.push(changeEvent)
                console;
            } else if (eventType == 'add-to-resource') {
                let changeEvent = new AddToResourceEvent()
                changeEvent.index = domEvent.getAttribute('index')
                changeEvent.valueId = domEvent.firstChild.getAttribute('eobject')
                changeEvent.index = domEvent.getAttribute('position')
                changeEvent.resource = this.resource
                this.resource.changeEvents.push(changeEvent)
                console;
            } else if (eventType == 'remove-from-resource') {
                let changeEvent = new RemoveFromResourceEvent()
                changeEvent.index = domEvent.getAttribute('index')
                changeEvent.valueId = domEvent.firstChild.getAttribute('eobject')
                changeEvent.index = domEvent.getAttribute('position')
                changeEvent.resource = this.resource
                this.resource.changeEvents.push(changeEvent)
                console;
            } else if (eventType == 'set-eattribute') {
                let changeEvent = new SetAttributeEvent()
                changeEvent.index = 0
                if (domEvent.getElementsByTagName('value')[0] != null) {
                    changeEvent.value = domEvent.getElementsByTagName('value')[0].getAttribute('literal')
                }
                if (domEvent.getElementsByTagName('old-value')[0] != null) {
                    changeEvent.oldValue = domEvent.getElementsByTagName('old-value')[0].getAttribute('literal')
                }
                changeEvent.resource = this.resource
                changeEvent.className = domEvent.getAttribute('eclass')
                changeEvent.targetId = domEvent.getAttribute('target')
                changeEvent.featureName = domEvent.getAttribute('name')
                this.resource.changeEvents.push(changeEvent)
                console;
            } else if (eventType == 'unset-eattribute') {
                let changeEvent = new UnsetAttributeEvent()
                changeEvent.index = 0
                changeEvent.resource = this.resource
                if (domEvent.getElementsByTagName('value')[0] != null) {
                    changeEvent.value = domEvent.getElementsByTagName('value')[0].getAttribute('literal')
                }
                if (domEvent.getElementsByTagName('old-value')[0] != null) {
                    changeEvent.oldValue = domEvent.getElementsByTagName('old-value')[0].getAttribute('literal')
                }
                changeEvent.className = domEvent.getAttribute('eclass')
                changeEvent.targetId = domEvent.getAttribute('target')
                changeEvent.featureName = domEvent.getAttribute('name')
                this.resource.changeEvents.push(changeEvent)
                console;
            } else if (eventType == 'set-ereference') {
                let changeEvent = new SetReferenceEvent()
                changeEvent.index = 0
                if (domEvent.getElementsByTagName('value')[0] != null) {
                    changeEvent.valueId = domEvent.getElementsByTagName('value')[0].getAttribute('eobject')
                }
                if (domEvent.getElementsByTagName('old-value')[0] != null) {
                    changeEvent.oldValueId = domEvent.getElementsByTagName('old-value')[0].getAttribute('eobject')
                }
                changeEvent.value = this.resource.getEObject(changeEvent.id)
                changeEvent.resource = this.resource
                changeEvent.className = domEvent.getAttribute('eclass')
                changeEvent.targetId = domEvent.getAttribute('target')
                changeEvent.featureName = domEvent.getAttribute('name')
                this.resource.changeEvents.push(changeEvent)
                console;
            } else if (eventType == 'unset-ereference') {
                let changeEvent = new UnsetReferenceEvent()
                changeEvent.index = 0
                if (domEvent.getElementsByTagName('value')[0] != null) {
                    changeEvent.valueId = domEvent.getElementsByTagName('value')[0].getAttribute('eobject')
                }
                if (domEvent.getElementsByTagName('old-value')[0] != null) {
                    changeEvent.oldValueId = domEvent.getElementsByTagName('old-value')[0].getAttribute('eobject')
                }
                changeEvent.value = null
                changeEvent.resource = this.resource
                changeEvent.className = domEvent.getAttribute('eclass')
                changeEvent.targetId = domEvent.getAttribute('target')
                changeEvent.featureName = domEvent.getAttribute('name')
                this.resource.changeEvents.push(changeEvent)
                console;
            } else if (eventType == 'add-to-ereference') {
                let changeEvent = new AddToReferenceEvent()
                changeEvent.valueId = domEvent.getElementsByTagName('value')[0].getAttribute('eobject')
                changeEvent.value = this.resource.getEObject(changeEvent.id)
                changeEvent.resource = this.resource
                changeEvent.className = domEvent.getAttribute('eclass')
                changeEvent.targetId = domEvent.getAttribute('target')
                changeEvent.featureName = domEvent.getAttribute('name')
                changeEvent.index = domEvent.getAttribute('position')
                this.resource.changeEvents.push(changeEvent)
                console;
            } else if (eventType == 'remove-from-ereference') {
                let changeEvent = new RemoveFromReference()
                changeEvent.valueId = domEvent.getElementsByTagName('value')[0].getAttribute('eobject')
                changeEvent.value = this.resource.getEObject(changeEvent.id)
                changeEvent.resource = this.resource
                changeEvent.className = domEvent.getAttribute('eclass')
                changeEvent.targetId = domEvent.getAttribute('target')
                changeEvent.featureName = domEvent.getAttribute('name')
                changeEvent.index = domEvent.getAttribute('position')
                this.resource.changeEvents.push(changeEvent)
                console;
            } else if (eventType == 'add-to-eattribute') {
                let changeEvent = new AddToAttributeEvent()
                if (domEvent.getElementsByTagName('value')[0] != null) {
                    changeEvent.value = domEvent.getElementsByTagName('value')[0].getAttribute('literal')
                }
                if (domEvent.getElementsByTagName('old-value')[0] != null) {
                    changeEvent.oldValue = domEvent.getElementsByTagName('old-value')[0].getAttribute('literal')
                }
                changeEvent.resource = this.resource
                changeEvent.className = domEvent.getAttribute('eclass')
                changeEvent.targetId = domEvent.getAttribute('target')
                changeEvent.featureName = domEvent.getAttribute('name')
                changeEvent.index = domEvent.getAttribute('position')
                this.resource.changeEvents.push(changeEvent)
                console;
            }

            // <add-to-ereference composite="_UAI88HMsEeme-P__sTghpg" eclass="Node" name="valNodes" index="0" target="O-1"><value eclass="Node" eobject="O-2"/></add-to-ereference>
        }
    }
}

/**
 * MAIN CODE
 */
var cbpPlayer = new CbpPlayer()
// var file = "cbp/origin.cbpxml"
var file = "cbp/process_1.cbpxml"
// var file = "cbp/process_1_new.cbpxml"
cbpPlayer.loadCbp(file)
