import Button from "@mui/material/Button";
import { Publish as ImportExportIcon } from "@mui/icons-material";

export default function TransactionsPage() {
    return (
        <>
            <Button variant="contained" onClick={() => {
                fetch("/api/user-info", {
                    method: "POST"
                })
                .then((resp) => {
                    console.log(resp)
                });
            }}>
                <ImportExportIcon /> Import
            </Button>
        </>
    );
}