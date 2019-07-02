let eventNumber = -1

let graphs = new Map()
let previousChangeEvent = null

function main() {
    let bpmn2 = new Bpmn2()
    bpmn2.initialise(graphs)
    let ecore = new Ecore()
    ecore.initialise(graphs)

    let elementTotal = document.getElementById("changeEventTotal")
    elementTotal.value = cbpPlayer.resource.changeEvents.length
};


let timer = null

function play() {
    document.getElementById('buttonPlay').disabled = true
    document.getElementById('buttonStop').disabled = false
    timer = setInterval(next, 1000)
}

function stop() {
    clearTimeout(timer)
    document.getElementById('buttonPlay').disabled = false
    document.getElementById('buttonStop').disabled = true

    // var encoder = new mxCodec();
    // var node1 = encoder.encode(graphs.get("ECORE").getModel());
    // mxUtils.popup(mxUtils.getPrettyXml(node1), false);
    // var node2 = encoder.encode(graphs.get("BPMN2").getModel());
    // mxUtils.popup(mxUtils.getPrettyXml(node2), false);
}

function exportEcore() {
    var encoder = new mxCodec();
    var node1 = encoder.encode(graphs.get("ECORE").getModel());
    mxUtils.popup(mxUtils.getPrettyXml(node1), false);
}

function exportBpmn2() {
    var encoder = new mxCodec();
    var node2 = encoder.encode(graphs.get("BPMN2").getModel());
    mxUtils.popup(mxUtils.getPrettyXml(node2), false);
}


function next() {
    if (eventNumber >= cbpPlayer.resource.changeEvents.length) {
        stop()
        return
    }

    if (eventNumber < cbpPlayer.resource.changeEvents.length) {

        eventNumber = eventNumber + 1
        let elementEvent = document.getElementById("changeEventNum")
        elementEvent.value = eventNumber

        let changeEvent = cbpPlayer.resource.changeEvents[eventNumber]
        if (changeEvent != null) {
            console.log("Replaying Event " + eventNumber + ": " + changeEvent.constructor.name)
            changeEvent.drawReplay(graphs)
            previousChangeEvent = changeEvent
        }
    }


};