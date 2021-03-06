function addGraph(graph, data) {
    function addNode(id, label) {
        if (!id || typeof id == "undefined") return null;
        var node = graph.getNode(id);
        if (!node) node = graph.addNode(id, label);
        return node;
    }

    for (let n in data.edges) {
        if (data.edges[n].source) {
            addNode(data.edges[n].source, data.edges[n].source_data );
        }
        if (data.edges[n].target) {
            addNode(data.edges[n].target, data.edges[n].target_data);
        }
    }

    for (n in data.edges) {
        var edge=data.edges[n];
        var found=false;
        graph.forEachLinkedNode(edge.source, function (node, link) {
            if (node.id===edge.target) found=true;
        });
        if (!found && edge.source && edge.target) graph.addLink(edge.source, edge.target);
    }
}

// Default
function onLoad() {
    // Step 0. Clear any pre-existing graph
    var e = document.getElementById('graphDiv')
    var child = e.lastElementChild;
    while (child) {
        e.removeChild(child);
        child = e.lastElementChild;
    }

    // Step 1. Create a graph:
    var graph = Viva.Graph.graph();

    var layout = Viva.Graph.Layout.forceDirected(graph, {
        springLength:100,
        springCoeff:0.0001,
        dragCoeff:0.02,
        gravity:-1
    });

    // Step 3. Customize node appearance.
    var graphics = Viva.Graph.View.svgGraphics();

    // we use this method to highlight all related links
    // when user hovers mouse over a node:
    highlightRelatedNodes = function(nodeId, isOn) {
        // just enumerate all related nodes and update link color:
        graph.forEachLinkedNode(nodeId, function(node, link){
            if (link && link.ui) {
                // link.ui is a special property of each link
                // points to the link presentation object.
                link.ui.attr('stroke', isOn ? 'white' : 'gray');
            }
        });
    };
    // This function let us override default node appearance and create
    // something better than blue dots:

    graphics.node(function(node) {
        // node.data holds custom object passed to graph.addNode():
        //var ui = Viva.Graph.svg('text').attr('y', '-4px').text(node.data.label);

        var ui = Viva.Graph.svg('g'),
            svgText = Viva.Graph.svg('text').attr('y', '-4px').text(node.data.label),
            img = Viva.Graph.svg('image')
                .attr('width', 32)
                .attr('height', 32)
                .link('/assets/images/colors/' + node.data.type + '.png');

        ui.append(svgText);
        ui.append(img);

        ui.addEventListener("mouseover", function( event ) {
            highlightRelatedNodes(node.id, true);
        }, false);
        ui.addEventListener("mouseleave", function( event ) {
            highlightRelatedNodes(node.id, false);
        }, false);

        ui.addEventListener("click", function() {
            if (!node) return;
            renderer.rerender();
            loadData(graph,node.id);
        });


        return ui;
    }).placeNode(function(nodeUI, pos) {
        // 'g' element doesn't have convenient (x,y) attributes, instead
        // we have to deal with transforms: http://www.w3.org/TR/SVG/coords.html#SVGGlobalTransformAttribute
        nodeUI.attr('transform',
            'translate(' +
            (pos.x - 16) + ',' + (pos.y - 16) +
            ')');
    });

    var renderer = Viva.Graph.View.renderer(graph,
        {
            layout:layout,
            graphics:graphics,
            container:document.getElementById('graphDiv'),
            renderLinks:true
        });


    renderer.run();

    var neoid = window.location.search.substring(1).split("=")[1];
    if ( neoid === undefined) {
        neoid = document.getElementById("value").value;
    }
    if ( neoid === "") {
        neoid = "customer-1";
    }

    var array = neoid.split('+');

    for (let id in array) {
        loadData(graph, array[id]);
    }

}

function loadData(graph,id) {
    var xhr = new XMLHttpRequest();
    xhr.responseType = "json";
    xhr.onreadystatechange = function() { //Call a function when the state changes.
        if (xhr.readyState === 4 && xhr.status === 200) {
            addGraph(graph, {edges: xhr.response });
        }
    }
    xhr.open('get', '/edges/' + id, true);
    xhr.send();
}

// Usecase 1
function onLoadProfile() {
    // Step 0. Clear any pre-existing graph
    var e = document.getElementById('graphDiv')
    var child = e.lastElementChild;
    while (child) {
        e.removeChild(child);
        child = e.lastElementChild;
    }

    // Step 1. Create a graph:
    var graph = Viva.Graph.graph();

    var layout = Viva.Graph.Layout.forceDirected(graph, {
        springLength:100,
        springCoeff:0.0001,
        dragCoeff:0.02,
        gravity:-1
    });

    // Step 3. Customize node appearance.
    var graphics = Viva.Graph.View.svgGraphics();

    // we use this method to highlight all related links
    // when user hovers mouse over a node:
    highlightRelatedNodes = function(nodeId, isOn) {
        // just enumerate all related nodes and update link color:
        graph.forEachLinkedNode(nodeId, function(node, link){
            if (link && link.ui) {
                // link.ui is a special property of each link
                // points to the link presentation object.
                link.ui.attr('stroke', isOn ? 'white' : 'gray');
            }
        });
    };
    // This function let us override default node appearance and create
    // something better than blue dots:

    graphics.node(function(node) {
        // node.data holds custom object passed to graph.addNode():
        //var ui = Viva.Graph.svg('text').attr('y', '-4px').text(node.data.label);

        var ui = Viva.Graph.svg('g'),
            svgText = Viva.Graph.svg('text').attr('y', '-4px').text(node.data.label),
            img = Viva.Graph.svg('image')
                .attr('width', 32)
                .attr('height', 32)
                .link('/assets/images/colors/' + node.data.type + '.png');

        ui.append(svgText);
        ui.append(img);

        ui.addEventListener("mouseover", function( event ) {
            highlightRelatedNodes(node.id, true);
        }, false);
        ui.addEventListener("mouseleave", function( event ) {
            highlightRelatedNodes(node.id, false);
        }, false);

        ui.addEventListener("click", function() {
            if (!node) return;
            renderer.rerender();
            loadProfileData(graph,node.id);
        });


        return ui;
    }).placeNode(function(nodeUI, pos) {
        // 'g' element doesn't have convenient (x,y) attributes, instead
        // we have to deal with transforms: http://www.w3.org/TR/SVG/coords.html#SVGGlobalTransformAttribute
        nodeUI.attr('transform',
            'translate(' +
            (pos.x - 16) + ',' + (pos.y - 16) +
            ')');
    });

    var renderer = Viva.Graph.View.renderer(graph,
        {
            layout:layout,
            graphics:graphics,
            container:document.getElementById('graphDiv'),
            renderLinks:true
        });


    renderer.run();

    var neoid = window.location.search.substring(1).split("=")[1];
    if ( neoid === undefined) {
        neoid = document.getElementById("value").value;
    }
    if ( neoid === "") {
        neoid = "amey";
    }

    var array = neoid.split('+');

    for (let id in array) {
        loadProfileData(graph, array[id]);
    }

}

function loadProfileData(graph,id) {
    var xhr = new XMLHttpRequest();
    xhr.responseType = "json";
    xhr.onreadystatechange = function() { //Call a function when the state changes.
        if (xhr.readyState === 4 && xhr.status === 200) {
            addGraph(graph, {edges: xhr.response });
        }
    }
    xhr.open('get', '/edges1/' + id, true);
    xhr.send();
}

// Usecase 2
function onLoadCustomerRecommendation() {
    // Step 0. Clear any pre-existing graph
    var e = document.getElementById('graphDiv')
    var child = e.lastElementChild;
    while (child) {
        e.removeChild(child);
        child = e.lastElementChild;
    }

    // Step 1. Create a graph:
    var graph = Viva.Graph.graph();

    var layout = Viva.Graph.Layout.forceDirected(graph, {
        springLength:100,
        springCoeff:0.0001,
        dragCoeff:0.02,
        gravity:-1
    });

    // Step 3. Customize node appearance.
    var graphics = Viva.Graph.View.svgGraphics();

    // we use this method to highlight all related links
    // when user hovers mouse over a node:
    highlightRelatedNodes = function(nodeId, isOn) {
        // just enumerate all related nodes and update link color:
        graph.forEachLinkedNode(nodeId, function(node, link){
            if (link && link.ui) {
                // link.ui is a special property of each link
                // points to the link presentation object.
                link.ui.attr('stroke', isOn ? 'white' : 'gray');
            }
        });
    };
    // This function let us override default node appearance and create
    // something better than blue dots:

    graphics.node(function(node) {
        // node.data holds custom object passed to graph.addNode():
        //var ui = Viva.Graph.svg('text').attr('y', '-4px').text(node.data.label);

        var ui = Viva.Graph.svg('g'),
            svgText = Viva.Graph.svg('text').attr('y', '-4px').text(node.data.label),
            img = Viva.Graph.svg('image')
                .attr('width', 32)
                .attr('height', 32)
                .link('/assets/images/colors/' + node.data.type + '.png');

        ui.append(svgText);
        ui.append(img);

        ui.addEventListener("mouseover", function( event ) {
            highlightRelatedNodes(node.id, true);
        }, false);
        ui.addEventListener("mouseleave", function( event ) {
            highlightRelatedNodes(node.id, false);
        }, false);

        ui.addEventListener("click", function() {
            if (!node) return;
            renderer.rerender();
            loadCustomerRecommendationData(graph,node.id);
        });


        return ui;
    }).placeNode(function(nodeUI, pos) {
        // 'g' element doesn't have convenient (x,y) attributes, instead
        // we have to deal with transforms: http://www.w3.org/TR/SVG/coords.html#SVGGlobalTransformAttribute
        nodeUI.attr('transform',
            'translate(' +
            (pos.x - 16) + ',' + (pos.y - 16) +
            ')');
    });

    var renderer = Viva.Graph.View.renderer(graph,
        {
            layout:layout,
            graphics:graphics,
            container:document.getElementById('graphDiv'),
            renderLinks:true
        });


    renderer.run();

    var neoid = window.location.search.substring(1).split("=")[1];
    if ( neoid === undefined) {
        neoid = document.getElementById("value").value;
    }
    if ( neoid === "") {
        neoid = "amey";
    }

    var array = neoid.split('+');

    for (let id in array) {
        loadCustomerRecommendationData(graph,array[id]);
    }

}

function loadCustomerRecommendationData(graph,id) {
    var xhr = new XMLHttpRequest();
    xhr.responseType = "json";
    xhr.onreadystatechange = function() { //Call a function when the state changes.
        if (xhr.readyState === 4 && xhr.status === 200) {
            addGraph(graph, {edges: xhr.response });
        }
    }
    xhr.open('get', '/edges2/' + id, true);
    xhr.send();
}

// Usecase 3
function onLoadBanker() {
    // Step 0. Clear any pre-existing graph
    var e = document.getElementById('graphDiv')
    var child = e.lastElementChild;
    while (child) {
        e.removeChild(child);
        child = e.lastElementChild;
    }

    // Step 1. Create a graph:
    var graph = Viva.Graph.graph();

    var layout = Viva.Graph.Layout.forceDirected(graph, {
        springLength:100,
        springCoeff:0.0001,
        dragCoeff:0.02,
        gravity:-1
    });

    // Step 3. Customize node appearance.
    var graphics = Viva.Graph.View.svgGraphics();

    // we use this method to highlight all related links
    // when user hovers mouse over a node:
    highlightRelatedNodes = function(nodeId, isOn) {
        // just enumerate all related nodes and update link color:
        graph.forEachLinkedNode(nodeId, function(node, link){
            if (link && link.ui) {
                // link.ui is a special property of each link
                // points to the link presentation object.
                link.ui.attr('stroke', isOn ? 'white' : 'gray');
            }
        });
    };
    // This function let us override default node appearance and create
    // something better than blue dots:

    graphics.node(function(node) {
        // node.data holds custom object passed to graph.addNode():
        //var ui = Viva.Graph.svg('text').attr('y', '-4px').text(node.data.label);

        var ui = Viva.Graph.svg('g'),
            svgText = Viva.Graph.svg('text').attr('y', '-4px').text(node.data.label),
            img = Viva.Graph.svg('image')
                .attr('width', 32)
                .attr('height', 32)
                .link('/assets/images/colors/' + node.data.type + '.png');

        ui.append(svgText);
        ui.append(img);

        ui.addEventListener("mouseover", function( event ) {
            highlightRelatedNodes(node.id, true);
        }, false);
        ui.addEventListener("mouseleave", function( event ) {
            highlightRelatedNodes(node.id, false);
        }, false);

        ui.addEventListener("click", function() {
            if (!node) return;
            renderer.rerender();
            loadProfileData(graph,node.id);
        });


        return ui;
    }).placeNode(function(nodeUI, pos) {
        // 'g' element doesn't have convenient (x,y) attributes, instead
        // we have to deal with transforms: http://www.w3.org/TR/SVG/coords.html#SVGGlobalTransformAttribute
        nodeUI.attr('transform',
            'translate(' +
            (pos.x - 16) + ',' + (pos.y - 16) +
            ')');
    });

    var renderer = Viva.Graph.View.renderer(graph,
        {
            layout:layout,
            graphics:graphics,
            container:document.getElementById('graphDiv'),
            renderLinks:true
        });


    renderer.run();

    var neoid = window.location.search.substring(1).split("=")[1];
    if ( neoid === undefined) {
        neoid = document.getElementById("value").value;
    }
    if ( neoid === "") {
        neoid = "chase";
    }

    var array = neoid.split('+');

    for (let id in array) {
        loadBankerData(graph, array[id]);
    }

}

function loadBankerData(graph,id) {
    var xhr = new XMLHttpRequest();
    xhr.responseType = "json";
    xhr.onreadystatechange = function() { //Call a function when the state changes.
        if (xhr.readyState === 4 && xhr.status === 200) {
            addGraph(graph, {edges: xhr.response });
        }
    }
    xhr.open('get', '/edges3/' + id, true);
    xhr.send();
}

