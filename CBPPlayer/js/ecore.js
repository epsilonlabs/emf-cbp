const MODEL_ECORE = "ECORE"

class MetaEcore {
    constructor() {
        this.contaimentReferences = []
        // this.contaimentReferences.push('valNodes')
        // this.contaimentReferences.push('rootElements')
        // this.contaimentReferences.push('definitions')
        // this.contaimentReferences.push('flowElements')
    }
}
const metaEcore = new MetaEcore()

class Ecore {
    constructor() {
        Ecore.prototype.layout = null
    }
}

Ecore.prototype.animate = function (graph) {
    let morph = new mxMorphing(graph);
    morph.addListener(mxEvent.DONE, function () {
        graph.getModel().endUpdate();
    });
    morph.startAnimation();
}

Ecore.prototype.EcoreDrawDeleteElementEvent = function(graph, event) {
    graph.getModel().beginUpdate();
    try {
        let parent = graph.getDefaultParent()

        let element = graph.getModel().getCell(event.id)
        if (element != null) {
            while (element.getEdgeCount() > 0) {
                let edge = element.getEdgeAt(0)
                let feature = edge.target

                while (feature.getEdgeCount() > 0) {
                    let featureEdge = feature.getEdgeAt(0)
                    let value = featureEdge.target
                    graph.getModel().remove(value)
                    graph.getModel().remove(featureEdge)
                }
                graph.getModel().remove(feature)
                graph.getModel().remove(edge)
            }
            graph.getModel().remove(element)

        }

        Ecore.prototype.layout.execute(parent);
    } catch (e) {
        console.log(e)
    } finally {
        Ecore.prototype.animate(graph)
    }
}

Ecore.prototype.EcoreDrawAddToResourceEvent = function (graph, event) {
    graph.getModel().beginUpdate();
    try {
        let parent = graph.getDefaultParent()
        let width = 40
        let height = 20
        let a = graph.container.clientWidth * graph.view.scale
        let b = graph.container.clientHeight * graph.view.scale
        let x = (-width + a) / 2
        let y = (-height + b) / 2

        let res = graph.getModel().getCell('resource')
        let edgeId = 'resource#' + event.valueId

        let element = graph.getModel().getCell(event.valueId)
        if (element == null) {
            element = graph.insertVertex(parent, event.valueId, event.valueId + ": " + event.className, x, y, width, height)
        }
        graph.insertEdge(parent, edgeId, event.index, res, element)

        Ecore.prototype.layout.execute(parent);
    } catch (e) {
        console.log(e)
    } finally {
        Ecore.prototype.animate(graph)
    }
}

Ecore.prototype.EcoreDrawRemoveFromResourceEvent= function (graph, event) {
    graph.getModel().beginUpdate();
    try {
        // let parent = graph.getDefaultParent()
        // let cell = graph.getModel().getCell(event.valueId)
        // graph.model.remove(cell)
        // Ecore.prototype.layout.execute(parent);
    } catch (e) {
        console.log(e)
    } finally {
        Ecore.prototype.animate(graph)
    }
}

Ecore.prototype.EcoreDrawSetAttributeEvent= function (graph, event) {
    graph.getModel().beginUpdate();
    try {
        let parent = graph.getDefaultParent()
        let width = 60
        let height = 20
        let a = graph.container.clientWidth * graph.view.scale
        let b = graph.container.clientHeight * graph.view.scale
        let x = (-width + a) / 2
        let y = (-height + b) / 2

        let featureId = event.targetId + '.' + event.featureName
        let featureEdgeId = event.targetId + '#' + event.featureName
        let valueId = event.targetId + '.' + event.featureName + '.0'
        let valueEdgeId = event.targetId + '#' + event.featureName + '#0'

        let ownerNode = graph.getModel().getCell(event.targetId)
        if (ownerNode == null) {
            ownerNode = graph.insertVertex(parent, event.targetId, event.targetId + ": " + event.className, x, y, width, height)
        }

        let featureNode = graph.getModel().getCell(featureId)
        if (featureNode == null) {
            featureNode = graph.insertVertex(parent, featureId, event.featureName, x, y, width, height, 'rounded=1;arcSize=50;')
            let edge = graph.insertEdge(parent, featureEdgeId, null, ownerNode, featureNode)
        }

        let value = event.value
        let valueNode = graph.getModel().getCell(valueId)
        if (valueNode == null) {
            valueNode = graph.insertVertex(parent, valueId, value, x, y, width, height, 'shape=hexagon;size=0.001;')
            let edge = graph.insertEdge(parent, valueEdgeId, null, featureNode, valueNode)
        } else {
            graph.getModel().setValue(valueNode, value)
        }

        Ecore.prototype.layout.execute(parent);
    } catch (e) {
        console.log(e)
    } finally {
        Ecore.prototype.animate(graph)
    }
}

Ecore.prototype.EcoreDrawUnsetAttributeEvent= function (graph, event) {
    graph.getModel().beginUpdate();
    try {
        let parent = graph.getDefaultParent()
        let width = 60
        let height = 20
        let a = graph.container.clientWidth * graph.view.scale
        let b = graph.container.clientHeight * graph.view.scale
        let x = (-width + a) / 2
        let y = (-height + b) / 2

        let featureId = event.targetId + '.' + event.featureName
        let featureEdgeId = event.targetId + '#' + event.featureName
        let oldValueId = event.targetId + '.' + event.featureName + '.0'
        let oldValueEdgeId = event.targetId + '#' + event.featureName + '#0'

        let ownerNode = graph.getModel().getCell(event.targetId)
        if (ownerNode == null) {
            ownerNode = graph.insertVertex(parent, event.targetId, event.targetId + ": " + event.className, x, y, width, height)
        }

        let featureNode = graph.getModel().getCell(featureId)
        if (featureNode == null) {
            featureNode = graph.insertVertex(parent, featureId, event.featureName, x, y, width, height)
            let edge = graph.insertEdge(parent, featureEdgeId, null, ownerNode, featureNode)
        }

        let value = event.value
        let valueNode = graph.getModel().getCell(oldValueId)
        let valueEdge = graph.getModel().getCell(oldValueEdgeId)
        if (valueNode != null) {
            graph.getModel().remove(valueNode)
        }
        if (valueEdge != null) {
            graph.getModel().remove(valueEdge)
        }

        graph.getModel().remove(featureNode)

        // if (ownerNode.getEdgeCount() == 0) {
        graph.getModel().remove(ownerNode)
        // }

        Ecore.prototype.layout.execute(parent);
    } catch
        (e) {
        console.log(e)
    } finally {
        Ecore.prototype.animate(graph)
    }
}

Ecore.prototype.EcoreDrawSetReferenceEvent= function (graph, event) {
    graph.getModel().beginUpdate();
    try {
        let parent = graph.getDefaultParent()
        let width = 60
        let height = 20
        let a = graph.container.clientWidth * graph.view.scale
        let b = graph.container.clientHeight * graph.view.scale
        let x = (-width + a) / 2
        let y = (-height + b) / 2

        let featureId = event.targetId + '.' + event.featureName
        let featureEdgeId = event.targetId + '#' + event.featureName
        let valueId = event.targetId + '.' + event.featureName + '.0'
        let valueEdgeId = event.targetId + '#' + event.featureName + '#0'

        let ownerNode = graph.getModel().getCell(event.targetId)
        if (ownerNode == null) {
            ownerNode = graph.insertVertex(parent, event.targetId, event.targetId + ": " + event.className, x, y, width, height)
        }

        let featureNode = graph.getModel().getCell(featureId)
        if (featureNode == null) {
            featureNode = graph.insertVertex(parent, featureId, event.featureName, x, y, width, height, 'rounded=1;arcSize=50;')
            let edge = graph.insertEdge(parent, featureEdgeId, null, ownerNode, featureNode)
        }


        let value = event.valueId
        let valueNode = graph.getModel().getCell(value)

        if (valueNode == null) {
            valueNode = graph.insertVertex(parent, event.valueId, event.valueId + ": " + event.value.className, x, y, width, height)
        }

        let edge = graph.insertEdge(parent, valueEdgeId, null, featureNode, valueNode)

        Ecore.prototype.layout.execute(parent);
    } catch
        (e) {
        console.log(e)
    } finally {
        Ecore.prototype.animate(graph)
    }
}

Ecore.prototype.EcoreDrawUnsetReferenceEvent= function (graph, event) {
    graph.getModel().beginUpdate();
    try {
        let parent = graph.getDefaultParent()
        let width = 60
        let height = 20
        let a = graph.container.clientWidth * graph.view.scale
        let b = graph.container.clientHeight * graph.view.scale
        let x = (-width + a) / 2
        let y = (-height + b) / 2

        let featureId = event.targetId + '.' + event.featureName
        let featureEdgeId = event.targetId + '#' + event.featureName
        let oldValueId = event.targetId + '.' + event.featureName + '.0'
        let oldValueEdgeId = event.targetId + '#' + event.featureName + '#0'

        let ownerNode = graph.getModel().getCell(event.targetId)
        if (ownerNode == null) {
            ownerNode = graph.insertVertex(parent, event.targetId, event.targetId + ": " + event.className, x, y, width, height)
        }

        let featureNode = graph.getModel().getCell(featureId)
        if (featureNode == null) {
            featureNode = graph.insertVertex(parent, featureId, event.featureName, x, y, width, height, 'rounded=1;arcSize=50;')
            let edge = graph.insertEdge(parent, featureEdgeId, null, ownerNode, featureNode)
        }

        let value = event.valueId
        let valueNode = graph.getModel().getCell(oldValueId)
        let valueEdge = graph.getModel().getCell(oldValueEdgeId)
        if (valueNode != null) {
            graph.getModel().remove(valueNode)
        }
        if (valueEdge != null) {
            graph.getModel().remove(valueEdge)
        }

        graph.getModel().remove(featureNode)

        if (ownerNode.getEdgeCount() == 0) {
            graph.getModel().remove(ownerNode)
        }

        Ecore.prototype.layout.execute(parent);
    } catch
        (e) {
        console.log(e)
    } finally {
        Ecore.prototype.animate(graph)
    }
}

Ecore.prototype.EcoreDrawAddToReferenceEvent= function (graph, event) {
    graph.getModel().beginUpdate();
    try {
        let parent = graph.getDefaultParent()
        let width = 60
        let height = 20
        let a = graph.container.clientWidth * graph.view.scale
        let b = graph.container.clientHeight * graph.view.scale
        let x = (-width + a) / 2
        let y = (-height + b) / 2

        let featureId = event.targetId + '.' + event.featureName
        let featureEdgeId = event.targetId + '#' + event.featureName
        let valueId = event.targetId + '.' + event.featureName + '.0'
        let valueEdgeId = event.targetId + '#' + event.featureName + '#0'

        let ownerNode = graph.getModel().getCell(event.targetId)
        if (ownerNode == null) {
            ownerNode = graph.insertVertex(parent, event.targetId, event.targetId + ": " + event.className, x, y, width, height)
        }

        let featureNode = graph.getModel().getCell(featureId)
        if (featureNode == null) {
            featureNode = graph.insertVertex(parent, featureId, event.featureName, x, y, width, height, 'rounded=1;arcSize=50;')
            let edge = graph.insertEdge(parent, featureEdgeId, null, ownerNode, featureNode)
        }

        let index = event.index
        let value = event.valueId
        let valueNode = graph.getModel().getCell(value)

        if (valueNode == null) {
            valueNode = graph.insertVertex(parent, event.valueId, event.valueId + ": " + event.value.className, x, y, width, height)
        }

        if (value != null) {
            let eObject = cbpPlayer.resource.getEObject(event.targetId);
            let feature = eObject.features.get(event.featureName);
            if (feature.isContainment == true) {
                let edge = null
                for (let i = 0; i < valueNode.getEdgeCount(); i++) {
                    edge = valueNode.getEdgeAt(i)
                    if (edge.target == valueNode) {
                        edge.target == null
                        graph.getModel().remove(edge)
                    }
                }
            }

            let edge = graph.insertEdge(parent, valueEdgeId, index, featureNode, valueNode)
        }

        Ecore.prototype.layout.execute(parent);
    } catch (e) {
        console.log(e)
    } finally {
        Ecore.prototype.animate(graph)
    }
}

// bpmn2:definitions=org.eclipse.Ecore.impl.DefinitionsImpl
Ecore.prototype.EcoreDrawAddToAttributeEvent= function (graph, event) {

    graph.getModel().beginUpdate();
    try {
        let parent = graph.getDefaultParent()
        let width = 60
        let height = 20
        let a = graph.container.clientWidth * graph.view.scale
        let b = graph.container.clientHeight * graph.view.scale
        let x = (-width + a) / 2
        let y = (-height + b) / 2

        let featureId = event.targetId + '.' + event.featureName
        let featureEdgeId = event.targetId + '#' + event.featureName
        let valueId = event.targetId + '.' + event.featureName + '.0'
        let valueEdgeId = event.targetId + '#' + event.featureName + '#0'

        let ownerNode = graph.getModel().getCell(event.targetId)
        if (ownerNode == null) {
            ownerNode = graph.insertVertex(parent, event.targetId, event.targetId + ": " + event.className, x, y, width, height)
        }

        let featureNode = graph.getModel().getCell(featureId)
        if (featureNode == null) {
            featureNode = graph.insertVertex(parent, featureId, event.featureName, x, y, width, height, 'rounded=1;arcSize=50;')
            let edge = graph.insertEdge(parent, featureEdgeId, null, ownerNode, featureNode)
        }

        let index = event.index
        let valueNode = graph.getModel().getCell(valueId)

        let value = null
        if (event.value.length > 20) {
            value = event.value.substring(0, 19)
        }
        if (valueNode == null) {
            valueNode = graph.insertVertex(parent, valueId, value, x, y, width, height)
        }

        let edge = graph.insertEdge(parent, valueEdgeId, index, featureNode, valueNode)


        Ecore.prototype.layout.execute(parent);
    } catch (e) {
        console.log(e)
    } finally {
        Ecore.prototype.animate(graph)
    }
}

Ecore.prototype.initialise = function(graphs) {

    let container = document.getElementById('graphContainer1')

    // Checks if the browser is supported
    if (!mxClient.isBrowserSupported()) {
        // Displays an error message if the browser is not supported.
        mxUtils.error('Browser is not supported!', 200, false);
    } else {
        // Disables the built-in context menu
        mxEvent.disableContextMenu(container);

        // Creates the graph inside the given container
        let graph = new mxGraph(container);
        graph.container.title = MODEL_ECORE
        graphs.set(MODEL_ECORE, graph)

        let style = graph.getStylesheet().getDefaultVertexStyle();
        style[mxConstants.STYLE_FILLCOLOR] = 'white';
        style[mxConstants.STYLE_STROKECOLOR] = 'black';
        style[mxConstants.STYLE_FONTCOLOR] = 'black';
        style[mxConstants.STYLE_FONTSIZE] = '10';

        style = graph.getStylesheet().getDefaultEdgeStyle()
        style[mxConstants.STYLE_STROKECOLOR] = 'black';
        style[mxConstants.STYLE_FONTCOLOR] = 'black';
        // style[mxConstants.STYLE_ENDARROW] = 'none';
        style[mxConstants.STYLE_FONTSIZE] = '10';

        //create layout
        // layout = new mxHierarchicalLayout(graph, mxConstants.DIRECTION_SOUTH)
        // layout = new mxFastOrganicLayout(graph)
        // layout = new mxCircleLayout(graph)
        Ecore.prototype.layout = new mxCompactTreeLayout(graph, false)
        // layout = new mxCompositeLayout(graph)
        // layout = new mxParallelEdgeLayout(graph)
        // layout = new mxPartitionLayout(graph)
        // layout = new mxStackLayout(graph)


        //get root (layer 0)
        let parent = graph.getDefaultParent();

        let width = 60
        let height = 20
        let a = graph.container.clientWidth * graph.view.scale
        let b = graph.container.clientHeight * graph.view.scale
        let x = (-width + a) / 2
        let y = 10

        graph.getModel().beginUpdate();
        graph.insertVertex(parent, 'resource', 'resource', x, y, width, height)
        graph.getModel().endUpdate();

        //execute layout
        Ecore.prototype.layout.execute(parent)
        console
    }
}

// assign appropriate drawing function to each event
for (let changeEvent of cbpPlayer.resource.changeEvents) {
    if (changeEvent instanceof DeleteElementEvent) {
        changeEvent.drawers.set(MODEL_ECORE, Ecore.prototype.EcoreDrawDeleteElementEvent)
    } else if (changeEvent instanceof AddToResourceEvent) {
        changeEvent.drawers.set(MODEL_ECORE, Ecore.prototype.EcoreDrawAddToResourceEvent)
    } else if (changeEvent instanceof RemoveFromResourceEvent) {
        changeEvent.drawers.set(MODEL_ECORE, Ecore.prototype.EcoreDrawRemoveFromResourceEvent)
    } else if (changeEvent instanceof SetAttributeEvent) {
        changeEvent.drawers.set(MODEL_ECORE, Ecore.prototype.EcoreDrawSetAttributeEvent)
    } else if (changeEvent instanceof UnsetAttributeEvent) {
        changeEvent.drawers.set(MODEL_ECORE, Ecore.prototype.EcoreDrawUnsetAttributeEvent)
    } else if (changeEvent instanceof SetReferenceEvent) {
        changeEvent.drawers.set(MODEL_ECORE, Ecore.prototype.EcoreDrawSetReferenceEvent)
    } else if (changeEvent instanceof UnsetReferenceEvent) {
        changeEvent.drawers.set(MODEL_ECORE, Ecore.prototype.EcoreDrawUnsetReferenceEvent)
    } else if (changeEvent instanceof AddToReferenceEvent) {
        changeEvent.drawers.set(MODEL_ECORE, Ecore.prototype.EcoreDrawAddToReferenceEvent)
    } else if (changeEvent instanceof AddToAttributeEvent) {
        changeEvent.drawers.set(MODEL_ECORE, Ecore.prototype.EcoreDrawAddToAttributeEvent)
    }
}
