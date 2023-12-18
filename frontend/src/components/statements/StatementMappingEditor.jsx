import VisNetwork from "@/components/statements/VisNetwork.jsx";
import Grid from "@mui/material/Unstable_Grid2";
import Button from "@mui/material/Button";
import NodeModal from "@/components/statements/NodeModal.jsx";

export default function StatementMappingEditor({ shown: shown = false, valueGroups: valueGroups = [] }) {


    var i = 0;

    return (
        <Grid justifyContent={"space-between"}  container columnSpacing={3}>
            <Grid xs={9}>
                <VisNetwork
                    nodes={valueGroups.map(group => {
                        return {
                            id: group.id,
                            label: group.name,
                            x: 0, y: (i++) * 10
                        }
                    })}
                    backgroundColor="#222222"
                    options={{
                        height: "1000px"
                    }}
                />
            </Grid>

            <Grid xs={3} container columnSpacing={1}>
                <Grid xs={6}>
                    <Button>Add Node</Button>
                </Grid>
                <Grid xs={6}>
                    <Button>Add Connection</Button>
                </Grid>
            </Grid>

            {/*<NodeModal open={true}></NodeModal>*/}

        </Grid>
    );
}