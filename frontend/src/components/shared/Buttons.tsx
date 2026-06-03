// src/shared/Buttons.tsx
interface ButtonsProps {
    text: string;
    onClick?: () => void;   // дарсан үед дуудагдах функц
    color?: string;
    size?: [number, number];
}

export default function Buttons({ text, onClick, color, size }: ButtonsProps) {
    const style = {
        backgroundColor: color || '#007bff',
        width: size ? `${size[0]}px` : 'auto',
        height: size ? `${size[1]}px` : 'auto',
        padding: '8px 16px',
        border: 'none',
        borderRadius: '4px',
        color: 'white',
        cursor: 'pointer',
    };
    return <button onClick={onClick} style={style}>{text}</button>;
}