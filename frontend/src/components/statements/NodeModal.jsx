import {Modal} from "@mui/material";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";

export default function NodeModal({ open }) {
    return (
        <Modal open={open}>
            <Box sx={{
                position: 'absolute',
                top: '50%',
                left: '50%',
                transform: 'translate(-50%, -50%)',
                bgcolor: 'background.paper',
                border: '2px solid #000',
                boxShadow: 24,
                pt: 2,
                px: 4,
                pb: 3,
                width: "400px"
            }}
            >
                <h2 id="child-modal-title">Text in a child modal</h2>
                <p id="child-modal-description">
                    Lorem ipsum, dolor sit amet consectetur adipisicing elit.
                </p>
                <Button>Close Child Modal</Button>
            </Box>
        </Modal>
    );
}