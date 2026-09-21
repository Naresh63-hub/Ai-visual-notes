import React from 'react';

interface BadgeProps {
  children: React.ReactNode;
  variant?: 'indigo' | 'amber' | 'emerald' | 'rose' | 'slate' | 'purple';
  size?: 'sm' | 'md';
  className?: string;
}

export const Badge: React.FC<BadgeProps> = ({
  children,
  variant = 'indigo',
  size = 'sm',
  className = '',
}) => {
  const variantStyles = {
    indigo: 'bg-indigo-50 text-indigo-700 border-indigo-200/70',
    amber: 'bg-amber-50 text-amber-800 border-amber-200/70',
    emerald: 'bg-emerald-50 text-emerald-800 border-emerald-200/70',
    rose: 'bg-rose-50 text-rose-800 border-rose-200/70',
    slate: 'bg-slate-100 text-slate-700 border-slate-200',
    purple: 'bg-purple-50 text-purple-700 border-purple-200/70',
  };

  const sizeStyles = {
    sm: 'text-[11px] px-2 py-0.5',
    md: 'text-xs px-2.5 py-1',
  };

  return (
    <span
      className={`inline-flex items-center gap-1 font-semibold rounded-full border ${variantStyles[variant]} ${sizeStyles[size]} ${className}`}
    >
      {children}
    </span>
  );
};
