import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";
import Typography from "@mui/material/Typography";
import * as React from "react";
import CardActions from "@mui/material/CardActions";
import Button from "@mui/material/Button";
import { AccountTree as AccountTreeIcon } from "@mui/icons-material";
import { Delete as DeleteIcon } from "@mui/icons-material";
import Grid from "@mui/material/Unstable_Grid2";

export default function StatementCard({ name, timeUploaded, id, onMap, onDelete }) {
    return (
        <Card id={`statement-${id}`}>
            <CardContent>
                <Typography gutterBottom>
                    {name} uploaded on {new Date(timeUploaded).toLocaleString("en-GB")}
                </Typography>
            </CardContent>
            <CardActions>
                <Button variant="contained" size="small" onClick={(e) => onMap(e, id)} startIcon={<AccountTreeIcon />}>
                    Map
                </Button>
                <Button variant="outlined" color="error" size="small" onClick={(e) => onDelete(e, id)} startIcon={<DeleteIcon />}>
                    Delete
                </Button>
            </CardActions>
        </Card>
    );
}