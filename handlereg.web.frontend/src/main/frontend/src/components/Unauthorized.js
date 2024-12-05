import React from 'react';
import { useSelector } from 'react-redux';
import { useGetLogoutMutation } from '../api';
import { Container } from './bootstrap/Container';


export default function Unauthorized() {
    const loginresultat = useSelector(state => state.loginresultat);
    const [ getLogout ] = useGetLogoutMutation();

    const onLogoutClicked = async () => {
        await getLogout();
    }

    return (
        <div>
            <nav className="flex items-center justify-between flex-wrap bg-slate-100 p-6">
                <a className="text-center block border border-blue-500 rounded py-2 bg-blue-500 hover:bg-blue-700 text-white" href="../.."><span title="chevron left" aria-hidden="true"></span>&nbsp;Gå hjem!</a>
                <h1 className="text-3xl font-bold">Ingen tilgang</h1>
                <div>&nbsp;</div>
            </nav>
            <Container>
                <p>Hei {loginresultat.brukernavn}! Du har ikke tilgang til denne applikasjonen</p>
                <p>Klikk &quot;Gå hjem&quot; for å navigere ut av applikasjonen, eller logg ut for å logge inn med en bruker som har tilgang</p>
                <form className="w-full max-w-lg mt-4 grid grid-flow-row auto-rows-max" onSubmit={ e => { e.preventDefault(); }}>
                    <div>
                        <div/>
                        <div>
                            <button className="w-full bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded" onClick={onLogoutClicked}>Logg ut</button>
                        </div>
                    </div>
                </form>
            </Container>
        </div>
    );
}
