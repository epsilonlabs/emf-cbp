const MODEL_BPMN2 = "BPMN2"

class MetaBpmn2 {
    constructor() {
        this.contaimentReferences = []
        // this.contaimentReferences.push('valNodes')
        // this.contaimentReferences.push('rootElements')
        // this.contaimentReferences.push('definitions')
        // this.contaimentReferences.push('flowElements')
    }
}

const metaBpmn2 = new MetaBpmn2()

class Bpmn2 {
    constructor() {
        Bpmn2.prototype.layout = null
        Bpmn2.prototype.runningY = 10
        Bpmn2.prototype.increment = 100
    }

}


Bpmn2.prototype.animate = function (graph) {
    let morph = new mxMorphing(graph);
    morph.addListener(mxEvent.DONE, function () {
        graph.getModel().endUpdate();
    });
    morph.startAnimation();
}

Bpmn2.prototype.Bpmn2DrawDeleteElementEvent = function (graph, event) {
    graph.getModel().beginUpdate();
    try {
        let parent = graph.getDefaultParent()
        let cell = graph.getModel().getCell(event.id)
        graph.removeCells([cell])
        Bpmn2.prototype.layout.execute(parent);
    } catch (e) {
        console.log(e)
    } finally {
        Bpmn2.prototype.animate(graph)
    }
}

Bpmn2.prototype.Bpmn2DrawAddToResourceEvent = function (graph, event) {
    graph.getModel().beginUpdate();
    try {
        let parent = graph.getDefaultParent()
        let width = 40
        let height = 20
        let a = graph.container.clientWidth * graph.view.scale
        let b = graph.container.clientHeight * graph.view.scale
        let x = (-width + a) / 2
        let y = (-height + b) / 2

        Bpmn2.prototype.layout.execute(parent);
    } catch (e) {
        console.log(e)
    } finally {
        Bpmn2.prototype.animate(graph)
    }
}

Bpmn2.prototype.Bpmn2DrawRemoveFromResourceEvent = function (graph, event) {
    graph.getModel().beginUpdate();
    try {
        // let parent = graph.getDefaultParent()
        // let cell = graph.getModel().getCell(event.valueId)
        // graph.model.remove(cell)
        // Bpmn2.prototype.layout.execute(parent);
    } catch (e) {
        console.log(e)
    } finally {
        Bpmn2.prototype.animate(graph)
    }
}

Bpmn2.prototype.Bpmn2DrawSetAttributeEvent = function (graph, event) {
    graph.getModel().beginUpdate();
    try {
        let element = cbpPlayer.resource.getEObject(event.targetId)
        if (!["StartEvent", "EndEvent", "SequenceFlow", "Task"].includes(element.className)) {
            return
        }
        let a = graph.container.clientWidth * graph.view.scale
        let b = graph.container.clientHeight * graph.view.scale

        let parent = graph.getDefaultParent()
        let valueNode = graph.getModel().getCell(event.targetId)

        let value = event.value
        if (valueNode == null) {
            if (element.className == "StartEvent") {
                let width = 30
                let height = 30
                let x = (-width + a) / 2
                let y = 10 + Bpmn2.prototype.runningY
                Bpmn2.prototype.runningY += Bpmn2.prototype.increment
                valueNode = graph.insertVertex(parent, event.targetId, event.targetId, x, y, width, height, "shape=ellipse;aspect=fixed;strokeWidth=1;")
            } else if (element.className == "EndEvent") {
                let width = 30
                let height = 30
                let x = (-width + a) / 2
                let y = 10 + Bpmn2.prototype.runningY
                Bpmn2.prototype.runningY += Bpmn2.prototype.increment
                valueNode = graph.insertVertex(parent, event.targetId, event.targetId, x, y, width, height, "shape=ellipse;aspect=fixed;strokeWidth=4;")
            } else if (element.className == "SequenceFlow") {
                valueNode = graph.insertEdge(parent, event.targetId, event.targetId, null, null)
            } else if (element.className == "Task") {
                let width = 90
                let height = 30
                let x = (-width + a) / 2
                let y = 10 + Bpmn2.prototype.runningY
                Bpmn2.prototype.runningY += Bpmn2.prototype.increment
                valueNode = graph.insertVertex(parent, event.targetId, event.targetId, x, y, width, height, "rounded=1;")
            }
        }
        if (valueNode != null) {
            graph.getModel().setValue(valueNode, value)
        }

        Bpmn2.prototype.layout.execute(parent);
    } catch (e) {
        console.log(e)
    } finally {
        Bpmn2.prototype.animate(graph)
    }
}

Bpmn2.prototype.Bpmn2DrawUnsetAttributeEvent = function (graph, event) {
    graph.getModel().beginUpdate();
    try {
        let parent = graph.getDefaultParent()
        let width = 60
        let height = 20
        let a = graph.container.clientWidth * graph.view.scale
        let b = graph.container.clientHeight * graph.view.scale
        let x = (-width + a) / 2
        let y = (-height + b) / 2


        Bpmn2.prototype.layout.execute(parent);
    } catch
        (e) {
        console.log(e)
    } finally {
        Bpmn2.prototype.animate(graph)
    }
}

Bpmn2.prototype.Bpmn2DrawSetReferenceEvent = function (graph, event) {
    graph.getModel().beginUpdate();
    try {
        let parent = graph.getDefaultParent()
        let width = 60
        let height = 20
        let a = graph.container.clientWidth * graph.view.scale
        let b = graph.container.clientHeight * graph.view.scale
        let x = (-width + a) / 2
        let y = (-height + b) / 2


        Bpmn2.prototype.layout.execute(parent);
    } catch
        (e) {
        console.log(e)
    } finally {
        Bpmn2.prototype.animate(graph)
    }
}

Bpmn2.prototype.Bpmn2DrawUnsetReferenceEvent = function (graph, event) {
    graph.getModel().beginUpdate();
    try {
        let parent = graph.getDefaultParent()
        let width = 60
        let height = 20
        let a = graph.container.clientWidth * graph.view.scale
        let b = graph.container.clientHeight * graph.view.scale
        let x = (-width + a) / 2
        let y = (-height + b) / 2


        Bpmn2.prototype.layout.execute(parent);
    } catch
        (e) {
        console.log(e)
    } finally {
        Bpmn2.prototype.animate(graph)
    }
}

Bpmn2.prototype.Bpmn2DrawAddToReferenceEvent = function (graph, event) {
    try {
        let element = cbpPlayer.resource.getEObject(event.valueId)
        if (!["StartEvent", "EndEvent", "SequenceFlow", "Task"].includes(element.className)) {
            return
        }

        graph.getModel().beginUpdate();

        let parent = graph.getDefaultParent()
        let a = graph.container.clientWidth * graph.view.scale
        let b = graph.container.clientHeight * graph.view.scale

        let container = cbpPlayer.resource.getEObject(event.targetId)
        let value = graph.getModel().getCell(event.valueId)
        if (container.className == "Process") {
            if (value == null) {
                if (element.className == "StartEvent") {
                    let width = 30
                    let height = 30
                    let x = (-width + a) / 2
                    let y = 10 + Bpmn2.prototype.runningY
                    Bpmn2.prototype.runningY += Bpmn2.prototype.increment
                    value = graph.insertVertex(parent, event.valueId, event.valueId, x, y, width, height, "shape=ellipse;aspect=fixed;strokeWidth=1;")
                } else if (element.className == "EndEvent") {
                    let width = 30
                    let height = 30
                    let x = (-width + a) / 2
                    let y = 10 + Bpmn2.prototype.runningY
                    Bpmn2.prototype.runningY += Bpmn2.prototype.increment
                    value = graph.insertVertex(parent, event.valueId, event.valueId, x, y, width, height, "shape=ellipse;aspect=fixed;strokeWidth=4;")
                } else if (element.className == "SequenceFlow") {
                    value = graph.insertEdge(parent, event.valueId, event.valueId, null, null)
                } else if (element.className == "Task") {
                    let width = 90
                    let height = 30
                    let x = (-width + a) / 2
                    let y = 10 + Bpmn2.prototype.runningY
                    Bpmn2.prototype.runningY += Bpmn2.prototype.increment
                    value = graph.insertVertex(parent, event.valueId, event.valueId, x, y, width, height, "rounded=1;")
                }
            }
        } else if (container.className == "StartEvent") {
            if (element.className == "SequenceFlow") {
                if (event.featureName == "incoming") {
                    if (value == null) {
                        value = graph.insertEdge(parent, event.valueId, event.valueId, null, null)
                    }
                    let target = graph.getModel().getCell(event.targetId)
                    value.target = target
                    if (value.source != null && value.target != null) {
                        let cell = graph.getModel().getCell(event.valueId)
                        graph.getModel().remove(cell)
                        value = graph.insertEdge(parent, event.valueId, event.valueId, value.source, value.target)
                    }
                } else if (event.featureName == "outgoing") {
                    if (value == null) {
                        value = graph.insertEdge(parent, event.valueId, event.valueId, null, null)
                    }
                    let source = graph.getModel().getCell(event.targetId)
                    value.source = source
                    if (value.source != null && value.target != null) {
                        let cell = graph.getModel().getCell(event.valueId)
                        graph.getModel().remove(cell)
                        value = graph.insertEdge(parent, event.valueId, event.valueId, value.source, value.target)
                    }
                }
            }
        } else if (container.className == "EndEvent") {
            if (element.className == "SequenceFlow") {
                if (event.featureName == "incoming") {
                    if (value == null) {
                        value = graph.insertEdge(parent, event.valueId, event.valueId, null, null)
                    }
                    let target = graph.getModel().getCell(event.targetId)
                    value.target = target
                    if (value.source != null && value.target != null) {
                        let cell = graph.getModel().getCell(event.valueId)
                        graph.getModel().remove(cell)
                        value = graph.insertEdge(parent, event.valueId, event.valueId, value.source, value.target)
                    }
                } else if (event.featureName == "outgoing") {
                    if (value == null) {
                        value = graph.insertEdge(parent, event.valueId, event.valueId, null, null)
                    }
                    let source = graph.getModel().getCell(event.targetId)
                    value.source = source
                    if (value.source != null && value.target != null) {
                        let cell = graph.getModel().getCell(event.valueId)
                        graph.getModel().remove(cell)
                        value = graph.insertEdge(parent, event.valueId, event.valueId, value.source, value.target)
                    }
                }
            }
        } else if (container.className == "Task") {
            if (element.className == "SequenceFlow") {
                if (event.featureName == "incoming") {
                    if (value == null) {
                        value = graph.insertEdge(parent, event.valueId, event.valueId, null, null)
                    }
                    let target = graph.getModel().getCell(event.targetId)
                    value.target = target
                    if (value.source != null && value.target != null) {
                        let cell = graph.getModel().getCell(event.valueId)
                        graph.getModel().remove(cell)
                        value = graph.insertEdge(parent, event.valueId, event.valueId, value.source, value.target)
                    }
                } else if (event.featureName == "outgoing") {
                    if (value == null) {
                        value = graph.insertEdge(parent, event.valueId, event.valueId, null, null)
                    }
                    let source = graph.getModel().getCell(event.targetId)
                    value.source = source
                    if (value.source != null && value.target != null) {
                        let cell = graph.getModel().getCell(event.valueId)
                        graph.getModel().remove(cell)
                        value = graph.insertEdge(parent, event.valueId, event.valueId, value.source, value.target)
                    }
                }
            }
        }
        Bpmn2.prototype.layout.execute(parent);
    } catch (e) {
        console.log(e)
    } finally {
        Bpmn2.prototype.animate(graph)
    }
}

// bpmn2:definitions=org.eclipse.bpmn2.impl.DefinitionsImpl
Bpmn2.prototype.Bpmn2DrawAddToAttributeEvent = function (graph, event) {

    graph.getModel().beginUpdate();
    try {
        let parent = graph.getDefaultParent()
        let width = 60
        let height = 20
        let a = graph.container.clientWidth * graph.view.scale
        let b = graph.container.clientHeight * graph.view.scale
        let x = (-width + a) / 2
        let y = (-height + b) / 2


        Bpmn2.prototype.layout.execute(parent);
    } catch (e) {
        console.log(e)
    } finally {
        Bpmn2.prototype.animate(graph)
    }
}

Bpmn2.prototype.initialise = function (graphs) {

    let container = document.getElementById('graphContainer2')

    // Checks if the browser is supported
    if (!mxClient.isBrowserSupported()) {
        // Displays an error message if the browser is not supported.
        mxUtils.error('Browser is not supported!', 200, false);
    } else {
        // Disables the built-in context menu
        mxEvent.disableContextMenu(container);

        // Creates the graph inside the given container
        let graph = new mxGraph(container);
        graph.container.title = MODEL_BPMN2
        graphs.set(MODEL_BPMN2, graph)

        let style = graph.getStylesheet().getDefaultVertexStyle();
        style[mxConstants.STYLE_FILLCOLOR] = 'white';
        style[mxConstants.STYLE_STROKECOLOR] = 'black';
        style[mxConstants.STYLE_FONTCOLOR] = 'black';
        style[mxConstants.STYLE_FONTSIZE] = '10';

        style = graph.getStylesheet().getDefaultEdgeStyle()
        style[mxConstants.STYLE_STROKECOLOR] = 'black';
        style[mxConstants.STYLE_FONTCOLOR] = 'black';
        style[mxConstants.STYLE_FONTSIZE] = '10';

        //create layout
        // layout = new mxHierarchicalLayout(graph, mxConstants.DIRECTION_SOUTH)
        // layout = new mxFastOrganicLayout(graph)
        // Bpmn2.prototype.layout = new mxCircleLayout(graph)
        Bpmn2.prototype.layout = new mxCompactTreeLayout(graph, false)
        // layout = new mxCompositeLayout(graph)
        // layout = new mxParallelEdgeLayout(graph)
        // layout = new mxPartitionLayout(graph)
        // layout = new mxStackLayout(graph)


        //get root (layer 0)
        let parent = graph.getDefaultParent();

        //execute layout
        Bpmn2.prototype.layout.execute(parent)
        console
    }
}

// assign appropriate drawing function to each event
for (let changeEvent of cbpPlayer.resource.changeEvents) {
    if (changeEvent instanceof DeleteElementEvent) {
        changeEvent.drawers.set(MODEL_BPMN2, Bpmn2.prototype.Bpmn2DrawDeleteElementEvent)
    } else if (changeEvent instanceof AddToResourceEvent) {
        changeEvent.drawers.set(MODEL_BPMN2, Bpmn2.prototype.Bpmn2DrawAddToResourceEvent)
    } else if (changeEvent instanceof RemoveFromResourceEvent) {
        changeEvent.drawers.set(MODEL_BPMN2, Bpmn2.prototype.Bpmn2DrawRemoveFromResourceEvent)
    } else if (changeEvent instanceof SetAttributeEvent) {
        changeEvent.drawers.set(MODEL_BPMN2, Bpmn2.prototype.Bpmn2DrawSetAttributeEvent)
    } else if (changeEvent instanceof UnsetAttributeEvent) {
        changeEvent.drawers.set(MODEL_BPMN2, Bpmn2.prototype.Bpmn2DrawUnsetAttributeEvent)
    } else if (changeEvent instanceof SetReferenceEvent) {
        changeEvent.drawers.set(MODEL_BPMN2, Bpmn2.prototype.Bpmn2DrawSetReferenceEvent)
    } else if (changeEvent instanceof UnsetReferenceEvent) {
        changeEvent.drawers.set(MODEL_BPMN2, Bpmn2.prototype.Bpmn2DrawUnsetReferenceEvent)
    } else if (changeEvent instanceof AddToReferenceEvent) {
        changeEvent.drawers.set(MODEL_BPMN2, Bpmn2.prototype.Bpmn2DrawAddToReferenceEvent)
    } else if (changeEvent instanceof AddToAttributeEvent) {
        changeEvent.drawers.set(MODEL_BPMN2, Bpmn2.prototype.Bpmn2DrawAddToAttributeEvent)
    }
}
