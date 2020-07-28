import React, { Component } from 'react';
class DynamicComponent extends Component {
   render() {
       const {title, content} = this.props;
       return (
           <div>
               <h1>{title}</h1>
               <p>{content}</p>
           </div>
       );
   }
}
export default DynamicComponent;
