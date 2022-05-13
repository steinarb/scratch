import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { push } from 'connected-react-router';
import { Helmet } from "react-helmet";
import { useSwipeable } from 'react-swipeable';
import { pictureTitle, formatMetadata } from './commonComponentCode';
import ModifyButton from './ModifyButton';
import DeleteButton from './DeleteButton';
import Previous from './Previous';
import Next from './Next';
import PictureNavbar from './PictureNavbar';
import PictureDescription from './PictureDescription';

export default function Picture(props) {
    const { item } = props;
    const parent = useSelector(state => (state.albumentries[item.parent] || {}).path);
    const previous = useSelector(state => state.previousentry[item.id]);
    const next = useSelector(state => state.nextentry[item.id]);
    const dispatch = useDispatch();
    const title = pictureTitle(item);
    const metadata = formatMetadata(item);
    const description = item.description ? metadata ? item.description + ' ' + metadata : item.description : metadata;
    const swipeHandlers = useSwipeable({
        onSwipedLeft: () => next && dispatch(push(next.path)),
        onSwipedRight: () => previous && dispatch(push(previous.path)),
    });

    return (
        <div>
            <Helmet>
                <title>{title}</title>
                <meta name="description" content={description}/>
            </Helmet>
            <PictureNavbar className="hide-on-landscape d-lg-none" item={item} parent={parent} title={title}/>
            <PictureNavbar className="d-none d-lg-block" item={item} parent={parent} title={title}/>
            <div className="btn-toolbar d-lg-none hide-on-landscape" role="toolbar">
                <Previous previous={previous} />
                <Next className="ml-auto" next={next} />
            </div>
            <div className="btn-group hide-on-landscape" role="group" aria-label="Modify album">
                <ModifyButton className="mx-1 my-1" item={item} />
                <DeleteButton className="mx-1 my-1" item={item} />
            </div>
            <div {...swipeHandlers}>
                <img className="img-fluid d-lg-none hide-on-landscape" src={item.imageUrl} />
                <img className="img-fluid-landscape img-center d-lg-none hide-on-portrait" src={item.imageUrl} />
                <div className="d-none d-lg-block">
                    <div className="row align-items-center d-flex justify-content-center">
                        <div className="col-auto"><Previous previous={previous} /></div>
                        <div className="col-auto">
                            <img className="img-fluid" src={item.imageUrl} />
                        </div>
                        <div className="col-auto"><Next className="ml-auto" next={next} /></div>
                    </div>
                </div>
                <PictureDescription className="hide-on-landscape d-lg-none" description={description}/>
                <PictureDescription className="d-none d-lg-block" description={description}/>
            </div>
        </div>
    );
}
