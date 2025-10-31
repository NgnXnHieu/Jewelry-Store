import './About.css';

function About() {
    return (
        <div className="about">
            <section className="about-section">
                <h2>Our Story</h2>
                <p>
                    Welcome to our Jewelry Store! We provide elegant and unique jewelry for every occasion.
                    Our mission is to bring beauty and style to your life.
                </p>
            </section>

            <section className="team">
                <h3>Meet the Team</h3>
                <div className="team-members">
                    <div className="member-card">Alice - Founder</div>
                    <div className="member-card">Bob - Designer</div>
                    <div className="member-card">Charlie - Marketing</div>
                </div>
            </section>
        </div>
    );
}

export default About;
