import {useRef, useEffect, useState} from "react";
import { Network } from "vis-network";

export default function VisNetwork({ nodes: nodes = [], edges: edges = [], backgroundColor: backgroundColor = "#ffffff", options: options = {} }) {
    const visJsRef = useRef(null);
    const [network, setNetwork] = useState();
  
    useEffect(() => {
        const network = visJsRef.current && new Network(visJsRef.current, { nodes, edges }, options);

        network.on("beforeDrawing",  function(ctx) {
            // save current translate/zoom
            ctx.save();

            // reset transform to identity
            ctx.setTransform(1, 0, 0, 1, 0, 0);

            ctx.fillStyle = backgroundColor;

            ctx.fillRect(0, 0, ctx.canvas.width, ctx.canvas.height)

            // restore old transform
            ctx.restore();
        })

    }, [visJsRef, backgroundColor, edges, nodes, options]);

    return <div ref={visJsRef} />;
}