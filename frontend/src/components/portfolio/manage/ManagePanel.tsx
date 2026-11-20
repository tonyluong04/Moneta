import { useState } from 'react';
import { Card } from '@/components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import AssetManager from '@/components/portfolio/manage/AssetManager';
import LiabilityManager from '@/components/portfolio/manage/LiabilityManager';

interface ManagePanelProps {
  /** The user's preferred currency, from the portfolio summary. */
  currency: string;
  onChanged: () => void;
}

/**
 * Below the category cards: two tabs over the assets and liabilities endpoints.
 * The active tab is local state; nothing links straight to one. Both tabs stay
 * mounted (forceMount, the inactive one hidden), so switching tabs keeps a
 * half-filled form or an edit in progress, and does not refetch the lists.
 */
export default function ManagePanel({ currency, onChanged }: ManagePanelProps) {
  const [tab, setTab] = useState('assets');

  return (
    <Card className="border-gray-200 p-5 shadow-sm">
      <Tabs value={tab} onValueChange={setTab}>
        <TabsList className="grid w-full grid-cols-2">
          <TabsTrigger value="assets">Manage Assets</TabsTrigger>
          <TabsTrigger value="liabilities">Manage Liabilities</TabsTrigger>
        </TabsList>
        <TabsContent value="assets" forceMount className="data-[state=inactive]:hidden">
          <AssetManager currency={currency} onChanged={onChanged} />
        </TabsContent>
        <TabsContent value="liabilities" forceMount className="data-[state=inactive]:hidden">
          <LiabilityManager currency={currency} onChanged={onChanged} />
        </TabsContent>
      </Tabs>
    </Card>
  );
}
