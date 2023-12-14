import { useRef, useEffect } from "react";
import { Network } from "vis-network";

export default function VisNetwork(props) {
    const visJsRef = useRef(null);

    const nodes = props.nodes || [];
    const edges = props.edges || [];
    const options = props.options || {};
  
    useEffect(() => {
      const network = visJsRef.current && new Network(visJsRef.current, { nodes, edges }, options);
    }, [visJsRef, nodes, edges]);

    return <div ref={visJsRef} />;
}