import React from 'react';
import { useSortable } from '@dnd-kit/sortable';
import { useDraggable, useDroppable } from '@dnd-kit/core';
import { CSS } from '@dnd-kit/utilities';
import { Button } from 'react-bootstrap';
import { FaTimes } from 'react-icons/fa';


export function Draggable(props) {
  const { attributes, listeners, setNodeRef, transform, transition } = useDraggable({
    id: 'draggable',
  });

  const style = transform
    ? {
      transform: `translate3d(${transform.x}px, ${transform.y}px, 0)`,
      cursor: 'grab',
      userSelect: 'none',
      boxShadow: '0 2px 4px rgba(0, 0, 0, 0.1)',
      zIndex: 1,
    }
    : undefined;

  return (
    <div ref={setNodeRef} style={style} {...attributes} {...listeners}>
      {props.children}
    </div>
  );
}


export function SortablePhoto({ id, preview, removePreviewImage }) {
  const { attributes, listeners, setNodeRef, transform, transition } = useSortable({
    id: `${id}`,
  });

  const style = {
    transform: CSS.Transform.toString(transform),
    position: 'relative',
  };

  const handleDelete = (event) => {
    event.stopPropagation(); // Stop the event propagation to prevent triggering the drag end event
    removePreviewImage(id);
  };

  return (
    <div
      ref={setNodeRef}
      style={style}
      {...attributes}
      {...listeners}
    >
      <img
        src={preview}
        alt={`Preview ${id}`}
        style={{
          width: '200px',
          height: '200px',
          objectFit: 'cover',
          border: '1px solid black',
          borderRadius: '4px',
          margin: '4px',
          boxShadow: '0 2px 4px rgba(0, 0, 0, 0.1)',
        }}
      />
      {id === 0 && <span style={{ position: 'absolute', left: '10px', bottom: '10px', color: 'white', fontWeight: 'bold', backgroundColor: 'rgba(0, 0, 0, 0.5)', padding: '2px 5px' }}>대표사진</span>}
      <Button
        data-no-dnd="true"
        onMouseDown={handleDelete}
        className="custom-button"
      >
        <span className="custom-button-text">
          <FaTimes />
        </span>
      </Button>
    </div>
  );
}
