import React from 'react';
import { Palette, HelpCircle, ChevronRight } from 'lucide-react';

export function SettingsView() {
  return (
    <div className="max-w-3xl mx-auto flex flex-col gap-8 pb-8 w-full">
      <div className="mb-2">
        <p className="font-label-sm text-label-sm text-outline uppercase tracking-wider mb-1">Preferences</p>
        <h2 className="font-headline-lg text-headline-lg text-on-surface font-semibold">Settings</h2>
      </div>

      <div className="flex flex-col gap-stack-gap">
        {/* Theme Settings */}
        <section className="bg-surface-container-lowest rounded-2xl p-container-padding shadow-[0px_4px_20px_rgba(0,0,0,0.04)]">
          <h3 className="font-label-sm text-label-sm text-on-surface-variant mb-4 uppercase tracking-wider">Appearance</h3>
          
          <button className="w-full flex items-center justify-between p-3 -mx-3 rounded-xl hover:bg-surface-container-high transition-colors text-left group">
            <div className="flex items-center gap-4">
              <div className="w-10 h-10 rounded-full bg-secondary-container text-on-secondary-container flex items-center justify-center shrink-0">
                <Palette size={20} />
              </div>
              <div className="flex-1">
                <div className="font-body-lg text-body-lg text-on-surface font-medium">Theme Color</div>
                <div className="font-body-md text-body-md text-on-surface-variant">Customize the primary accent color</div>
              </div>
            </div>
            <ChevronRight size={20} className="text-outline group-hover:text-on-surface transition-colors" />
          </button>
        </section>

        {/* About & Instructions */}
        <section className="bg-surface-container-lowest rounded-2xl p-container-padding shadow-[0px_4px_20px_rgba(0,0,0,0.04)]">
          <h3 className="font-label-sm text-label-sm text-on-surface-variant mb-4 uppercase tracking-wider">Help & About</h3>
          
          <button className="w-full flex items-center justify-between p-3 -mx-3 rounded-xl hover:bg-surface-container-high transition-colors text-left group">
            <div className="flex items-center gap-4">
              <div className="w-10 h-10 rounded-full bg-primary-fixed text-on-primary-fixed flex items-center justify-center shrink-0">
                <HelpCircle size={20} />
              </div>
              <div className="flex-1">
                <div className="font-body-lg text-body-lg text-on-surface font-medium">Instructions</div>
                <div className="font-body-md text-body-md text-on-surface-variant">Learn how to use Clarity effectively</div>
              </div>
            </div>
            <ChevronRight size={20} className="text-outline group-hover:text-on-surface transition-colors" />
          </button>
        </section>
      </div>
    </div>
  );
}
