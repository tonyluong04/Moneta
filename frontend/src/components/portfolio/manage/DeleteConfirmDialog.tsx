import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';

interface DeleteConfirmDialogProps {
  /** The row about to go, or null when the dialog is closed. */
  itemName: string | null;
  deleting: boolean;
  onCancel: () => void;
  onConfirm: () => void;
}

/** Delete is permanent (no soft-delete), so it asks first. */
export default function DeleteConfirmDialog({
  itemName,
  deleting,
  onCancel,
  onConfirm,
}: DeleteConfirmDialogProps) {
  return (
    <Dialog open={itemName !== null} onOpenChange={(open) => !open && !deleting && onCancel()}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Delete “{itemName}”?</DialogTitle>
          <DialogDescription>This can’t be undone.</DialogDescription>
        </DialogHeader>
        <DialogFooter className="gap-2">
          <Button variant="outline" onClick={onCancel} disabled={deleting}>
            Cancel
          </Button>
          <Button variant="destructive" onClick={onConfirm} disabled={deleting}>
            {deleting ? 'Deleting…' : 'Delete'}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
