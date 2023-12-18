import Button from "@mui/material/Button";
import Box from "@mui/material/Box";
import { CloudUpload as CloudUploadIcon } from "@mui/icons-material";
import VisuallyHiddenInput from "@/components/VisuallyHiddenInput.jsx";
import toast from "react-hot-toast";
import utils from "@/utils.js";
import Grid from "@mui/material/Unstable_Grid2";
import {useEffect, useState} from "react";
import {Stack} from "@mui/material";
import VisNetwork from "@/components/statements/VisNetwork.jsx";
import StatementCard from "@/components/statements/StatementCard.jsx";
import Carousel from "react-material-ui-carousel";
import NodeModal from "@/components/statements/NodeModal.jsx";
import StatementMappingEditor from "@/components/statements/StatementMappingEditor.jsx";


export default function StatementsPage() {

    const [statements, setStatements] = useState([]);

    const [valueGroups, setValueGroups] = useState([]);

    useEffect(() => {
        utils.performRequest("/api/statements")
            .then(resp => resp.json())
            .then(({ result }) => setStatements(result));
    }, []);

    function fetchStatements() {
        utils.performRequest("/api/statements")
            .then(resp => resp.json())
            .then(({ result }) => setStatements(result));
    }

    async function uploadStatement({ target }) {
        let file = target.files[0];

        let formData = new FormData();
        formData.append("file", file);

        await toast.promise(
            utils.performRequest("/api/statements/uploadSheet", {
                method: "POST",
                body: formData
            }),
            {
                loading: "Uploading...",
                success: () => {
                    fetchStatements();

                    return "Upload successful!";
                },
                error: (err) => `Uh oh, something went wrong: ${err}`
            }
        );
    }

    async function mapStatement(e, statementId) {
        await toast.promise(
            utils.performRequest(`/api/statements/${statementId}/transactionValueGroups`)
                .then(res => res.json())
                .then(json => setValueGroups(json.result)),
            {
                loading: "Preparing...",
                success: "Ready",
                error: (err) => `Uh oh, something went wrong: ${err}`
            }
        );
    }

    async function deleteStatement(e, statementId) {
        await toast.promise(
            utils.performRequest(`/api/statements/${statementId}`, { method: "DELETE" }),
            {
                loading: "Deleting...",
                success: () => {
                    fetchStatements();

                    return "Success";
                },
                error: (err) => `Uh oh, something went wrong: ${err}`
            }
        )
    }

    function createCarouselItems() {
        let carouselItemCount = Math.ceil(statements.length / 4) || 1;

        let carouselItems = [];

        for (let i = 0; i < carouselItemCount; i++) {
            let firstIndex = i * 4;
            let lastIndex = firstIndex + 4;

            if (lastIndex > statements.length) {
                lastIndex = statements.length;
            }

            carouselItems.push(
                <Grid key={i} container spacing={2}>
                    {
                        statements.slice(firstIndex, lastIndex).map((statement) => (
                            <Grid key={statement.id} xs={3}>
                                <StatementCard
                                    id={statement.id}
                                    timeUploaded={statement.timeUploaded}
                                    name={statement.name}
                                    onMap={mapStatement}
                                    onDelete={deleteStatement}
                                />
                            </Grid>
                        ))
                    }
                </Grid>
            );
        }

        return carouselItems;
    }

    var i = 0;

    return (
        <Stack>
            <div>
                <Grid container rowSpacing={3} columnSpacing={3}>
                    <Grid xs={3}>
                        <Button component="label" variant="contained" startIcon={<CloudUploadIcon />}>
                            Upload Statement
                            <VisuallyHiddenInput type="file" onChange={uploadStatement} />
                        </Button>
                    </Grid>

                    <Grid xs={9}></Grid>

                    <Grid xs={12}>
                        <Carousel
                            cycleNavigation
                            fullHeightHover
                            swipe
                            animation={"slide"}
                            duration={100}
                            autoPlay={false}
                        >
                            {createCarouselItems()}
                        </Carousel>
                    </Grid>

                    <Grid xs={12}>
                        <StatementMappingEditor valueGroups={valueGroups} />
                    </Grid>
                </Grid>
            </div>
        </Stack>
    );
}